package westmeijer.oskar.server.service;

import java.net.Socket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import westmeijer.oskar.server.service.model.ClientActivity;
import westmeijer.oskar.server.service.model.HistorizedEventType;
import westmeijer.oskar.shared.model.response.RelayedClientActivity.ActivityType;

@Slf4j
@RequiredArgsConstructor
public class ConnectionProcessor {

  // TODO: singleton
  private final HistorizedEventService historizedEventService;
  private final ClientInitializer clientInitializer;
  private final ClientRegister clientRegister;

  void process(Socket socket) {
    var clientListener = clientInitializer.init(socket, historizedEventService, clientRegister);
    clientRegister.registerClient(clientListener);
    var clientDetails = clientListener.getClientDetails();

    var historizedActivity = new ClientActivity(HistorizedEventType.CLIENT_CONNECTED, clientDetails);
    historizedEventService.recordMessage(historizedActivity);

    var clients = clientRegister.getClients();
    OutgoingNotificationService.notifyClientActivity(clients, historizedActivity, ActivityType.CONNECTED);

    // TODO: think about management over thread pool. Tag thread name with clientId
    Thread thread = new Thread(clientListener);
    thread.start();
    log.info("Client connected. client: {}, clients count: {}", clientDetails, clientRegister.getClientsCount());
  }

}
