package westmeijer.oskar.client.service.server;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import westmeijer.oskar.client.loggers.ChatLogger;
import westmeijer.oskar.client.loggers.ServerLogger;
import westmeijer.oskar.shared.model.response.ChatHistoryResponse;
import westmeijer.oskar.shared.model.response.ClientListResponse;
import westmeijer.oskar.shared.model.response.RelayedChatMessage;
import westmeijer.oskar.shared.model.response.RelayedClientActivity;
import westmeijer.oskar.shared.model.response.ServerMessage;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServerProcessor {

  @Getter
  private static final ServerProcessor instance = new ServerProcessor();

  public void process(ServerMessage message) {
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
