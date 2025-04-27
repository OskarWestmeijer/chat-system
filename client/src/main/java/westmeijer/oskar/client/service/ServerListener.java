package westmeijer.oskar.client.service;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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
  @Setter
  private boolean isConnected = false;

  private final Socket socket;

  private final ObjectInputStream objectInputStream;

  @Getter
  private final ObjectOutputStream objectOutputStream;

  private final ExecutorService executorService = Executors.newSingleThreadExecutor();


  void connect() {
    // TODO: when server shuts down, this application does not. Control with future?
    Runnable listenForMessagesTask = () -> {
      try {
        while (isConnected) {
          ServerMessage serverMessage = (ServerMessage) objectInputStream.readObject();
          processReceivedMessage(serverMessage);
        }
      } catch (Exception e) {
        log.error("Exception, while listening for server stream.", e);
      }
    };
    executorService.submit(listenForMessagesTask);
    isConnected = true;
  }

  void disconnect() {
    isConnected = false;
    executorService.shutdownNow();
    StreamProvider.streamCloser.apply(objectInputStream);
    StreamProvider.streamCloser.apply(objectOutputStream);
    StreamProvider.streamCloser.apply(socket);
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
