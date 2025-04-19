package westmeijer.oskar.server.client;

import static westmeijer.oskar.shared.model.system.SystemEventType.DISCONNECTION_COMMAND;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.time.Instant;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import westmeijer.oskar.server.repository.PublicEventHistoryRepository;
import westmeijer.oskar.server.service.ConnectionsListener;
import westmeijer.oskar.shared.model.ClientDetails;
import westmeijer.oskar.shared.model.history.ClientActivity;
import westmeijer.oskar.shared.model.history.ClientMessage;
import westmeijer.oskar.shared.model.history.HistoryEvent;
import westmeijer.oskar.shared.model.history.HistoryEventType;
import westmeijer.oskar.shared.model.system.ClientChatRequest;
import westmeijer.oskar.shared.model.system.RelayedChatMessage;
import westmeijer.oskar.shared.model.system.SystemEvent;

@Slf4j
public class ClientListener implements Runnable {

  private final PublicEventHistoryRepository publicEventHistoryRepository;
  private final Socket socket;
  private final InputStream inputStream;
  private final ObjectInputStream objectInputStream;
  private final OutputStream outputStream;
  @Getter
  private final ObjectOutputStream objectOutputStream;
  private boolean isConnected = true;

  @Getter
  private final ClientDetails clientDetails;

  public ClientListener(Socket socket) {
    try {
      this.socket = socket;
      this.outputStream = socket.getOutputStream();
      this.objectOutputStream = new ObjectOutputStream(outputStream);
      this.objectOutputStream.flush();
      this.inputStream = socket.getInputStream();
      this.objectInputStream = new ObjectInputStream(inputStream);
      this.publicEventHistoryRepository = PublicEventHistoryRepository.getInstance();
      this.clientDetails = ClientDetails.from(socket.getInetAddress().getHostAddress());
    } catch (Exception e) {
      log.error("Exception thrown.", e);
      throw new RuntimeException(e);
    }
  }

  @Override
  public void run() {
    try {
      Object message;
      log.info("Waiting for messages from client.");
      while (isConnected && (message = objectInputStream.readObject()) != null) {
        log.info("Processing message: {}", message);
        processMessage(message);
      }
    } catch (Exception e) {
      log.error("Exception thrown while listening for client.", e);
      disconnect();
    }
  }

  private void processMessage(Object message) {
    if (message instanceof ClientChatRequest) {
      processClientChatRequest((ClientChatRequest) message);
    } else if (message instanceof SystemEvent) {
      processSystemEvent((SystemEvent) message);
    } else {
      throw new IllegalArgumentException("Did not find processing path for input. %s".formatted(message));
    }
  }

  private void processClientChatRequest(ClientChatRequest request) {
    var clientMessage = new ClientMessage(request.getMessage(), clientDetails);
    publicEventHistoryRepository.insertMessage(clientMessage);
    var relayedMessage = new RelayedChatMessage(clientDetails.getId(), request.getMessage());
    relayMessageToOtherClients(relayedMessage);
  }

  private void processSystemEvent(SystemEvent systemEvent) {
    switch (systemEvent.getType()) {
      case LIST_CLIENTS -> sendClientList();
      case CHAT_HISTORY -> sendChatHistory();
      case DISCONNECTION_REQUEST -> invokeClientDisconnect();
      case DISCONNECTION_COMMAND -> clientInvokedUngracefulDisconnect();
    }
  }

  private void invokeClientDisconnect() {
    try {
      var systemEvent = SystemEvent.builder()
          .type(DISCONNECTION_COMMAND)
          .recordedAt(Instant.now());
      objectOutputStream.writeObject(systemEvent);
      objectOutputStream.flush();
      disconnect();
    } catch (IOException e) {
      log.error("Exception thrown, while relaying message to client.", e);
    }
  }

  private void relayMessageToOtherClients(RelayedChatMessage message) {
    ConnectionsListener.CONNECTED_CLIENT_CONTROLLERS.stream()
        .filter(client -> client != this)
        .forEach(client -> relayMessage(client, message));
  }

  private void relayMessage(ClientListener client, RelayedChatMessage message) {
    try {
      client.getObjectOutputStream().writeObject(message);
      client.getObjectOutputStream().flush();
    } catch (IOException e) {
      log.error("Exception thrown, while relaying message to client.", e);
    }
  }

  private void sendChatHistory() {
    try {
      for (HistoryEvent historyEvent : publicEventHistoryRepository.getHistory()) {
        // TODO: use history response model here
        objectOutputStream.writeObject(historyEvent);
        objectOutputStream.flush();
      }
    } catch (IOException e) {
      log.error("Exception thrown, while sharing chat history.", e);
    }
  }

  private void sendClientList() {
    try {
      for (ClientListener client : ConnectionsListener.CONNECTED_CLIENT_CONTROLLERS) {
        log.info("Writing client info. client: {}", client.getClientDetails());
        objectOutputStream.writeObject(client.getClientDetails());
        objectOutputStream.flush();
      }
    } catch (IOException e) {
      log.error("Exception thrown, while sharing chat history.", e);
    }
  }

  private void clientInvokedUngracefulDisconnect() {
    log.info("Client invoked ungraceful disconnect: {}", clientDetails);
    disconnect();
  }

  private void disconnect() {
    try {
      isConnected = false;
      objectInputStream.close();
      inputStream.close();
      objectOutputStream.close();
      outputStream.close();
      socket.close();
      ConnectionsListener.CONNECTED_CLIENT_CONTROLLERS.remove(this);
      var clientActivity = new ClientActivity(HistoryEventType.CLIENT_DISCONNECTED, clientDetails);
      publicEventHistoryRepository.insertMessage(clientActivity);
      log.info("Disconnecting client. clients left: {}, disconnected client: {}", ConnectionsListener.CONNECTED_CLIENT_CONTROLLERS.size(),
          clientDetails);
    } catch (Exception e) {
      log.error("Exception thrown while disconnecting the client.", e);
    }
  }
}
