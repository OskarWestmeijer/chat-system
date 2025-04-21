package westmeijer.oskar.server.client;

import java.io.EOFException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import westmeijer.oskar.server.service.ClientService;
import westmeijer.oskar.server.service.EventNotificationService;
import westmeijer.oskar.server.service.HistorizedEventService;
import westmeijer.oskar.server.service.model.ClientActivity;
import westmeijer.oskar.server.service.model.ClientDetails;
import westmeijer.oskar.server.service.model.ClientMessage;
import westmeijer.oskar.server.service.model.HistorizedEventType;
import westmeijer.oskar.shared.model.request.ClientChatRequest;
import westmeijer.oskar.shared.model.request.ClientCommandRequest;
import westmeijer.oskar.shared.model.response.ChatHistoryResponse;
import westmeijer.oskar.shared.model.response.ClientListResponse;
import westmeijer.oskar.shared.model.response.RelayedClientActivity.ActivityType;

@Slf4j
@EqualsAndHashCode
public class ClientListener implements Runnable {

  private final HistorizedEventService historizedEventService;
  private final ClientService clientService;

  private final Socket socket;
  private final InputStream inputStream;
  private final ObjectInputStream objectInputStream;
  private final OutputStream outputStream;
  @Getter
  private final ObjectOutputStream objectOutputStream;
  private boolean isConnected = true;

  @Getter
  private final ClientDetails clientDetails;

  public ClientListener(Socket socket, ClientDetails clientDetails) {
    try {
      this.socket = socket;
      this.outputStream = socket.getOutputStream();
      this.objectOutputStream = new ObjectOutputStream(outputStream);
      this.objectOutputStream.flush();
      this.inputStream = socket.getInputStream();
      this.objectInputStream = new ObjectInputStream(inputStream);
      this.historizedEventService = HistorizedEventService.getInstance();
      this.clientService = ClientService.getInstance();
      this.clientDetails = clientDetails;
    } catch (Exception e) {
      log.error("Exception thrown.", e);
      throw new RuntimeException(e);
    }
  }

  @Override
  public void run() {
    try {
      Object message;
      log.info("Waiting for messages from client: {}", clientDetails);
      while (isConnected && (message = objectInputStream.readObject()) != null) {
        log.info("Processing message: {}, client: {}", message, clientDetails);
        // TODO: create message processing unit
        processMessage(message);
      }
    } catch (EOFException e) {
      log.info("Catched exception: {}", e.getClass());
      disconnect();
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
    historizedEventService.recordMessage(clientMessage);
    var otherClients = clientService.getClients(List.of(this));
    EventNotificationService.notifyChatMessage(otherClients, clientDetails, clientMessage);
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
    EventNotificationService.sendMessage(this, new ChatHistoryResponse(history));
  }

  private void sendClientList() {
    var clientDetailsList = clientService.getClients().stream()
        .map(client -> client.getClientDetails().getClientLog())
        .toList();
    EventNotificationService.sendMessage(this, new ClientListResponse(clientDetailsList));
  }

  private void disconnect() {
    try {
      isConnected = false;
      objectInputStream.close();
      inputStream.close();
      objectOutputStream.close();
      outputStream.close();
      socket.close();

      clientService.unregisterClient(this);

      var clientActivity = new ClientActivity(HistorizedEventType.CLIENT_DISCONNECTED, clientDetails);
      historizedEventService.recordMessage(clientActivity);

      EventNotificationService.notifyClientActivity(clientService.getClients(), clientActivity, ActivityType.DISCONNECTED);
    } catch (Exception e) {
      log.error("Exception thrown while disconnecting the client.", e);
    }
  }
}
