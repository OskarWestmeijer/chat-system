package westmeijer.oskar.server.client;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import westmeijer.oskar.server.service.ClientRegister;
import westmeijer.oskar.server.service.HistorizedEventService;
import westmeijer.oskar.server.service.OutgoingNotificationService;
import westmeijer.oskar.server.service.model.ClientActivity;
import westmeijer.oskar.server.service.model.ClientDetails;
import westmeijer.oskar.server.service.model.ClientMessage;
import westmeijer.oskar.shared.model.request.ClientChatRequest;
import westmeijer.oskar.shared.model.request.ClientCommandRequest;
import westmeijer.oskar.shared.model.response.ChatHistoryResponse;
import westmeijer.oskar.shared.model.response.ClientListResponse;

@Slf4j
@RequiredArgsConstructor
public class ClientProcessor {

  private final HistorizedEventService historizedEventService;
  private final ClientRegister clientRegister;
  private final ClientDetails clientDetails;

  void processMessage(Object message) {
    log.info("Processing message: {}, client: {}", message, clientDetails);
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
    historizedEventService.recordMessage(clientMessage);
    var otherClients = clientRegister.getClients(List.of(this.clientDetails));
    OutgoingNotificationService.notifyChatMessage(otherClients, clientDetails, clientMessage);
  }

  private void processClientCommandRequest(ClientCommandRequest request) {
    switch (request.getEventType()) {
      case LIST_CLIENTS -> sendClientList();
      case CHAT_HISTORY -> sendChatHistory();
    }
  }

  private void sendChatHistory() {
    // TODO: fix evaluation of history type
    var history = historizedEventService.getHistory().stream()
        .map(historyEvent -> switch (historyEvent) {
          case ClientActivity activity -> activity.getHistorizedLog();
          case ClientMessage message -> message.getHistorizedLog();
          default -> throw new IllegalStateException("Unexpected value: " + historyEvent);
        })
        .toList();
    OutgoingNotificationService.sendMessage(getSelf(), new ChatHistoryResponse(history));
  }

  private void sendClientList() {
    var clientDetailsList = clientRegister.getClients().stream()
        .map(client -> client.getClientDetails().getClientLog())
        .toList();
    OutgoingNotificationService.sendMessage(getSelf(), new ClientListResponse(clientDetailsList));
  }

  private ClientListener getSelf() {
    return clientRegister.getClient(this.clientDetails)
        .orElseThrow(() -> new RuntimeException("Client not registered: %s".formatted(clientDetails)));
  }

}
