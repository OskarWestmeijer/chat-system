package westmeijer.oskar.server.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import westmeijer.oskar.server.repository.PublicEventHistoryRepository;
import westmeijer.oskar.server.repository.history.ClientActivity;
import westmeijer.oskar.server.repository.history.ClientDetails;
import westmeijer.oskar.server.repository.history.ClientMessage;
import westmeijer.oskar.server.repository.history.HistoryEventType;
import westmeijer.oskar.server.service.ConnectionsListener;
import westmeijer.oskar.shared.model.request.ClientChatRequest;
import westmeijer.oskar.shared.model.request.ClientCommandRequest;
import westmeijer.oskar.shared.model.response.ChatHistoryResponse;
import westmeijer.oskar.shared.model.response.ClientListResponse;
import westmeijer.oskar.shared.model.response.RelayedChatMessage;
import westmeijer.oskar.shared.model.response.RelayedClientActivity;
import westmeijer.oskar.shared.model.response.RelayedClientActivity.ACTIVITY_TYPE;

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
        // TODO
        // create message processing unit
        // create universal output stream unit
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
    } else if (message instanceof ClientCommandRequest) {
      processClientCommandRequest((ClientCommandRequest) message);
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

  private void processClientCommandRequest(ClientCommandRequest request) {
    switch (request.getEventType()) {
      case LIST_CLIENTS -> sendClientList();
      case CHAT_HISTORY -> sendChatHistory();
    }
  }

  private void relayMessageToOtherClients(RelayedChatMessage message) {
    ConnectionsListener.CONNECTED_CLIENT_CONTROLLERS.stream()
        .filter(client -> client != this)
        .forEach(client -> relayMessage(client, message));
  }

  public static void relayMessage(ClientListener client, Object message) {
    try {
      client.getObjectOutputStream().writeObject(message);
      client.getObjectOutputStream().flush();
    } catch (IOException e) {
      log.error("Exception thrown, while relaying message to client.", e);
    }
  }

  private void sendChatHistory() {
    // TODO: fix evaluation of history type
    var history = publicEventHistoryRepository.getHistory().stream()
        .map(historyEvent -> "%s %s: %s".formatted(historyEvent.getRecordedAt().truncatedTo(ChronoUnit.SECONDS), historyEvent.getId(),
            historyEvent.getEvent()))
        .toList();
    try {
      objectOutputStream.writeObject(new ChatHistoryResponse(history));
      objectOutputStream.flush();
    } catch (IOException e) {
      log.error("Exception thrown, while sharing chat history.", e);
    }
  }

  private void sendClientList() {
    // TODO: make nicer
    var clients = ConnectionsListener.CONNECTED_CLIENT_CONTROLLERS.stream()
        .map(client -> client.getClientDetails().getClientLog())
        .toList();
    try {
      objectOutputStream.writeObject(new ClientListResponse(clients));
      objectOutputStream.flush();
    } catch (IOException e) {
      log.error("Exception thrown, while sharing chat history.", e);
    }
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

      var relayedClientActivity = new RelayedClientActivity(this.clientDetails.getId(), ACTIVITY_TYPE.DISCONNECTED, Instant.now());
      ConnectionsListener.CONNECTED_CLIENT_CONTROLLERS.forEach(c -> ClientListener.relayMessage(c, relayedClientActivity));
      log.info("Disconnecting client. clients left: {}, disconnected client: {}", ConnectionsListener.CONNECTED_CLIENT_CONTROLLERS.size(),
          clientDetails);
    } catch (Exception e) {
      log.error("Exception thrown while disconnecting the client.", e);
    }
  }
}
