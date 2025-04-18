package de.schlaumeijer.service;

import de.schlaumeijer.repository.ChatMessageRepository;
import de.schlaumeijer.repository.ChatMessageRepositoryImpl;
import de.schlaumeijer.service.model.ChatMessageDto;
import de.schlaumeijer.service.model.ClientConnectionDto;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientListener implements Runnable {

  private final ChatMessageRepository chatMessageRepository;
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
      this.chatMessageRepository = ChatMessageRepositoryImpl.getInstance();
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

  private void processChatMessageDto(ChatMessageDto chatMessageDto) {
    chatMessageDto.setClientConnectionDto(clientConnectionDto);
    // TODO: use modern switch, use enum for commands.
    switch (chatMessageDto.getMessage()) {
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
        listMessages();
        break;
      default:
        relayMessageToOtherClients(chatMessageDto);
    }
  }

  private void relayMessageToOtherClients(ChatMessageDto chatMessageDto) {
    chatMessageRepository.insertMessage(chatMessageDto);
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

  private void listMessages() {
    try {
      for (ChatMessageDto chatMessageDto : chatMessageRepository.readAllMessages()) {
        objectOutputStream.writeObject(chatMessageDto);
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
