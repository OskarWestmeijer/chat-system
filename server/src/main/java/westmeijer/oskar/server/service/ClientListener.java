package westmeijer.oskar.server.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import westmeijer.oskar.server.repository.PublicEventHistoryRepository;
import westmeijer.oskar.shared.model.ChatMessageDto;
import westmeijer.oskar.shared.model.ClientConnectionDto;
import westmeijer.oskar.shared.model.PublicEvent;

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
  private final ClientConnectionDto clientConnectionDto;

  public ClientListener(Socket socket) {
    try {
      this.socket = socket;
      this.outputStream = socket.getOutputStream();
      this.objectOutputStream = new ObjectOutputStream(outputStream);
      this.objectOutputStream.flush();
      this.inputStream = socket.getInputStream();
      this.objectInputStream = new ObjectInputStream(inputStream);
      this.publicEventHistoryRepository = PublicEventHistoryRepository.getInstance();
      this.clientConnectionDto = ClientConnectionDto.from(socket.getInetAddress().getHostAddress());
    } catch (IOException e) {
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
        log.info("Received message from clientId: {}, ip: {}", clientConnectionDto.getId(), clientConnectionDto.getIp());
        processMessage(message);
      }
    } catch (IOException e) {
      log.error("Exception thrown while listening for client.", e);
      disconnect();
    } catch (ClassNotFoundException e) {
      log.error("Exception thrown while listening for client.", e);
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
      log.info("Disconnecting client: {}. client count left: {}", clientConnectionDto,
          ConnectionsListener.CONNECTED_CLIENT_CONTROLLERS.size());
    } catch (IOException e) {
      log.error("Exception thrown while disconnecting the client.", e);
    }
  }

  private void processMessage(Object message) {
    log.info("Processing message: {}", message);
    if (message instanceof ChatMessageDto) {
      processChatMessageDto(((ChatMessageDto) message));
    } else {
      throw new IllegalArgumentException("Did not find processing path for input.");
    }
  }

  private void processChatMessageDto(ChatMessageDto receivedMessage) {
    var enrichedMessage = receivedMessage.toBuilder()
        .client(clientConnectionDto)
        .build();
    // TODO: use modern switch, use enum for commands.
    switch (enrichedMessage.getMessage()) {
      case "/quit":
        log.info("Recognized message as disconnection command.");
        disconnect();
        break;
      case "/clients":
        log.info("Recognized message as list clients command.");
        listClients();
        break;
      case "/history":
        log.info("Recognized message as list messages command.");
        sendHistory();
        break;
      default:
        relayMessageToOtherClients(enrichedMessage);
    }
  }

  private void relayMessageToOtherClients(ChatMessageDto chatMessageDto) {
    publicEventHistoryRepository.insertMessage(chatMessageDto);
    ConnectionsListener.CONNECTED_CLIENT_CONTROLLERS.stream()
        .filter(client -> client != this)
        .forEach(client -> relayMessage(client, chatMessageDto));
  }

  private void relayMessage(ClientListener client, ChatMessageDto messageDto) {
    try {
      client.getObjectOutputStream().writeObject(messageDto);
      client.getObjectOutputStream().flush();
    } catch (IOException e) {
      log.error("Exception thrown, while relaying message to client.", e);
    }
  }

  private void sendHistory() {
    try {
      for (PublicEvent publicEvent : publicEventHistoryRepository.getHistory()) {
        objectOutputStream.writeObject(publicEvent);
        objectOutputStream.flush();
      }
    } catch (IOException e) {
      log.error("Exception thrown, while sharing chat history.", e);
    }
  }

  private void listClients() {
    try {
      for (ClientListener client : ConnectionsListener.CONNECTED_CLIENT_CONTROLLERS) {
        log.info("Writing client info. client: {}", client);
        objectOutputStream.writeObject(client.getClientConnectionDto());
        objectOutputStream.flush();
      }
    } catch (IOException e) {
      log.error("Exception thrown, while sharing chat history.", e);
    }
  }

}
