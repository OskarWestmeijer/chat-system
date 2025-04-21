package westmeijer.oskar.server.service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import lombok.extern.slf4j.Slf4j;
import westmeijer.oskar.server.service.model.ClientActivity;
import westmeijer.oskar.server.service.model.HistorizedEventType;
import westmeijer.oskar.shared.model.response.RelayedClientActivity.ActivityType;

@Slf4j
public class ConnectionsListener {

  private final ServerSocket server;

  private final ClientService clientService = ClientService.getInstance();
  private final HistorizedEventService historizedEventService = HistorizedEventService.getInstance();

  public ConnectionsListener(int port) throws IOException {
    this.server = new ServerSocket(port);
  }

  public void listenForConnection() {
    log.info("Created chat server. port: {}, connected clients count: {}", server.getLocalPort(), clientService.getClientsCount());
    try {
      while (true) {
        var socket = server.accept();
        handleNewConnection(socket);
      }
    } catch (IOException e) {
      log.error("Exception thrown.", e);
    }
  }

  private void handleNewConnection(Socket socket) {
    var clientListener = clientService.registerClient(socket);
    var clientDetails = clientListener.getClientDetails();

    var historizedActivity = new ClientActivity(HistorizedEventType.CLIENT_CONNECTED, clientDetails);
    historizedEventService.recordMessage(historizedActivity);

    var clients = clientService.getClients();
    EventNotificationService.notifyClientActivity(clients, historizedActivity, ActivityType.CONNECTED);

    // TODO: think about management over thread pool. Tag thread name with clientId
    Thread thread = new Thread(clientListener);
    thread.start();
    log.info("Client connected. client: {}, clients count: {}", clientDetails, clientService.getClientsCount());
  }

}