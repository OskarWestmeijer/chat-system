package westmeijer.oskar.server.service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import westmeijer.oskar.server.client.ClientListener;
import westmeijer.oskar.server.repository.PublicEventHistoryRepository;
import westmeijer.oskar.server.repository.history.ClientActivity;
import westmeijer.oskar.server.repository.history.HistoryEventType;
import westmeijer.oskar.shared.model.response.RelayedClientActivity;
import westmeijer.oskar.shared.model.response.RelayedClientActivity.ACTIVITY_TYPE;

@Slf4j
public class ConnectionsListener {

  private final ServerSocket server;

  // TODO: move this list to repo
  public static final List<ClientListener> CONNECTED_CLIENT_CONTROLLERS = new ArrayList<>();

  public ConnectionsListener(int port) throws IOException {
    this.server = new ServerSocket(port);
    log.info("Created chat server. port: {}, connected clients count: {}", port, CONNECTED_CLIENT_CONTROLLERS.size());
  }

  public void listenForConnection() {
    try {
      while (true) {
        newClientConnection(server.accept());
      }
    } catch (IOException e) {
      log.error("Exception thrown.", e);
    }
  }

  private void newClientConnection(Socket socket) {

    /*
    TODO:
    - create client
    - add client to known connection pool
    - historize client joined event
    - relay client joined event
     */

    log.info("Client joined chat. clientIp: {}", socket.getInetAddress());
    ClientListener clientListener = new ClientListener(socket);
    var clientDetails = clientListener.getClientDetails();
    var relayedActivity = new RelayedClientActivity(clientDetails.getId(), ACTIVITY_TYPE.CONNECTED, clientDetails.getConnectedAt());
    var historizedActivity = new ClientActivity(HistoryEventType.CLIENT_CONNECTED, clientDetails);
    CONNECTED_CLIENT_CONTROLLERS
        .forEach(client -> ClientListener.relayMessage(client, relayedActivity));
    CONNECTED_CLIENT_CONTROLLERS.add(clientListener);
    PublicEventHistoryRepository.getInstance().insertMessage(historizedActivity);
    // TODO: think about management over thread pool.
    Thread thread = new Thread(clientListener);
    thread.start();
    log.info("Connected clients count: {}", CONNECTED_CLIENT_CONTROLLERS.size());
  }

}