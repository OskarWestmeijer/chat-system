package de.schlaumeijer.bl;

import de.schlaumeijer.dal.ChatMessageRepositoryImpl;
import de.schlaumeijer.dal.ClientConnectionRepositoryImpl;
import de.schlaumeijer.shared.ChatMessageDto;
import de.schlaumeijer.shared.ClientConnectionDto;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.UUID;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientCtrl implements Runnable {

  private final ChatMessageRepository chatMessageRepository;

  private final ClientConnectionRepository clientConnectionRepository;

  private final Socket socket;

  private final InputStream inputStream;

  private final ObjectInputStream objectInputStream;

  private final OutputStream outputStream;

  @Getter
  private ObjectOutputStream objectOutputStream;

  private boolean isConnected = true;

  private final MessageCtrl messageCtrl;

  public ClientCtrl(Socket socket) {
    try {
      this.socket = socket;
      this.outputStream = socket.getOutputStream();
      this.objectOutputStream = new ObjectOutputStream(outputStream);
      this.objectOutputStream.flush();
      this.inputStream = socket.getInputStream();
      this.objectInputStream = new ObjectInputStream(inputStream);
      this.messageCtrl = MessageCtrl.getInstance(this);
      this.clientConnectionRepository = new ClientConnectionRepositoryImpl();
      this.chatMessageRepository = new ChatMessageRepositoryImpl();
    } catch (IOException e) {
      log.error("Exception thrown.", e);
      throw new RuntimeException(e);
    }
  }

  @Override
  public void run() {
    try {
      Object message;
      log.info("Waiting for messages.");
      while (isConnected && (message = objectInputStream.readObject()) != null) {
        log.info("Received message from: {}", socket.getInetAddress());
        processInput(message);
      }
    } catch (IOException e) {
      disconnect();
      log.error("Exception thrown.", e);
    } catch (ClassNotFoundException e) {
      log.error("Exception thrown.", e);
    }
  }

  public void disconnect() {
    try {
      log.info("Client left the chat. ip: {}", socket.getInetAddress());
      isConnected = false;
      objectInputStream.close();
      inputStream.close();
      objectOutputStream.close();
      outputStream.close();
      socket.close();
      ServerCtrl.CONNECTED_CLIENT_CTRLS.remove(this);
    } catch (IOException e) {
      log.error("Exception thrown.", e);
    }
  }

  private void processInput(Object object) {
    if (object instanceof ChatMessageDto) {
      processChatMessageDto(((ChatMessageDto) object));
    } else if (object instanceof ClientConnectionDto) {
      processClientConnectionDto(((ClientConnectionDto) object));
    }

  }

  private void processChatMessageDto(ChatMessageDto chatMessageDto) {
    chatMessageDto.setDate(new Date(System.currentTimeMillis()));
    chatMessageDto.setUuid(UUID.randomUUID());
    chatMessageDto.setSenderIp(socket.getInetAddress().getHostAddress());
    chatMessageDto.setSenderName("Bob");
    chatMessageRepository.insertMessage(chatMessageDto);
    switch (chatMessageDto.getMessage()) {
      case "q!":
        disconnect();
        break;
      case "list -c":
        listClients();
        break;
      case "list -ch":
        listClientsHistory();
        break;
      case "list -m":
        listMessages();
        break;
      default:
        sendMessageToOtherClients(chatMessageDto);
    }
  }

  private void sendMessageToOtherClients(ChatMessageDto chatMessageDto) {
    messageCtrl.sendMessageToOtherClients(chatMessageDto);
  }

  private void listMessages() {
    messageCtrl.listMessages(chatMessageRepository.readAllMessages());
  }

  private void listClientsHistory() {
    messageCtrl.listClientHistory(clientConnectionRepository.readHistoryOfConnections());
  }

  private void listClients() {
    log.warn("Not yet implemented");
    // messageCtrl.listClients(ServerCtrl.CONNECTED_CLIENT_CTRLS);
  }

  private void processClientConnectionDto(ClientConnectionDto clientConnectionDto) {
    log.warn("Not yet implemented");
  }

}
