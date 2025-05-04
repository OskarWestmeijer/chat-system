package westmeijer.oskar.server.client;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import westmeijer.oskar.server.service.ClientRegister;
import westmeijer.oskar.server.service.HistorizedEventService;
import westmeijer.oskar.server.service.OutgoingNotificationService;
import westmeijer.oskar.server.service.model.ClientActivity;
import westmeijer.oskar.server.service.model.ClientDetails;
import westmeijer.oskar.server.service.model.HistorizedEventType;
import westmeijer.oskar.shared.model.response.RelayedClientActivity.ActivityType;

@Slf4j
@Getter
@RequiredArgsConstructor
public class ClientListener implements Runnable {

  private final HistorizedEventService historizedEventService;
  private final ClientRegister clientRegister;
  private final ClientStreamProvider clientStreamProvider;
  private final ClientProcessor clientProcessor;
  private final ClientDetails clientDetails;

  @Override
  public void run() {
    try {
      log.info("Waiting for messages from client: {}", clientDetails);
      while (clientStreamProvider.isConnected()) {
        var message = clientStreamProvider.readFromStream();
        clientProcessor.processMessage(message);
      }
    } catch (Exception e) {
      log.error("Exception thrown while listening for client.", e);
      disconnect();
    }
  }

  private void disconnect() {
    log.info("Disconnecting client: {}", clientDetails);
    try {
      clientStreamProvider.setConnected(false);
      clientStreamProvider.closeStreams();

      clientRegister.unregisterClient(this);

      var clientActivity = new ClientActivity(HistorizedEventType.CLIENT_DISCONNECTED, clientDetails);
      historizedEventService.recordMessage(clientActivity);

      OutgoingNotificationService.notifyClientActivity(clientRegister.getClients(), clientActivity, ActivityType.DISCONNECTED);
    } catch (Exception e) {
      log.error("Exception thrown while disconnecting the client.", e);
    }
  }
}
