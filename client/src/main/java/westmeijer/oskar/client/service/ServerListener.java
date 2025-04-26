package westmeijer.oskar.client.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import westmeijer.oskar.client.loggers.ChatLogger;
import westmeijer.oskar.client.loggers.ServerLogger;
import westmeijer.oskar.shared.model.response.ChatHistoryResponse;
import westmeijer.oskar.shared.model.response.ClientListResponse;
import westmeijer.oskar.shared.model.response.RelayedChatMessage;
import westmeijer.oskar.shared.model.response.RelayedClientActivity;
import westmeijer.oskar.shared.model.response.ServerMessage;

@Slf4j
@RequiredArgsConstructor
public class ServerListener {

  @Getter
  private boolean isConnected = false;

  private final String serverIp;

  private final int serverPort;

  private Socket socket;

  private InputStream inputStream;

  private ObjectInputStream objectInputStream;

  private OutputStream outputStream;

  @Getter
  private ObjectOutputStream objectOutputStream;

  private final ExecutorService executorService = Executors.newSingleThreadExecutor();

  void connect() {
    try {
      this.socket = new Socket(serverIp, serverPort);
      this.isConnected = true;
      outputStream = socket.getOutputStream();
      objectOutputStream = new ObjectOutputStream(outputStream);
      objectOutputStream.flush();
      inputStream = socket.getInputStream();
      objectInputStream = new ObjectInputStream(inputStream);
      ServerLogger.log("Connected to Server at " + serverIp + " " + serverPort);
      listenForMessagesLoop();
    } catch (IOException e) {
      log.error("Error while connecting to server.", e);
      throw new RuntimeException(e);
    }
  }

  void disconnect() {
    try {
      isConnected = false;
      objectInputStream.close();
      objectOutputStream.close();
      outputStream.close();
      inputStream.close();
      socket.close();
      executorService.shutdownNow();
      System.exit(0);
    } catch (Exception e) {
      log.error("Exception thrown, while disconnecting.", e);
      throw new RuntimeException(e);
    }
  }

  private void listenForMessagesLoop() {
    // TODO: when server shuts down, this application does not
    Runnable listenForMessagesTask = () -> {
      try {
        while (isConnected) {
          ServerMessage serverMessage = (ServerMessage) objectInputStream.readObject();
          processReceivedMessage(serverMessage);
        }
      } catch (Exception e) {
        log.error("Exception thrown. Shutting down client.", e);
        throw new RuntimeException(e);
      }
    };
    executorService.submit(listenForMessagesTask);
  }

  private void processReceivedMessage(ServerMessage message) {
    log.trace("Received message: {}", message);
    switch (message) {
      case ChatHistoryResponse chatHistoryResponse -> processChatHistoryResponse(chatHistoryResponse);
      case ClientListResponse clientListResponse -> processClientListResponse(clientListResponse);
      case RelayedChatMessage relayedChatMessage -> processClientMessage(relayedChatMessage);
      case RelayedClientActivity relayedClientActivity -> processClientActivity(relayedClientActivity);
      default -> throw new RuntimeException("Could not process received message. %s".formatted(message));
    }
  }

  private void processChatHistoryResponse(ChatHistoryResponse message) {
    ServerLogger.log("-- START OF HISTORY --");
    message.getMessageHistory().forEach(ServerLogger::log);
    ServerLogger.log("-- END OF HISTORY --");
    ServerLogger.log("");
  }

  private void processClientListResponse(ClientListResponse clientListResponse) {
    ServerLogger.log("-- START OF CLIENT LIST --");
    clientListResponse.getClients().forEach(ServerLogger::log);
    ServerLogger.log("-- END OF CLIENT LIST --");
    ServerLogger.log("");
  }

  private void processClientMessage(RelayedChatMessage message) {
    ChatLogger.log(message.getClientLog());
  }

  private void processClientActivity(RelayedClientActivity message) {
    ServerLogger.log(message.getClientLog());
  }

}
