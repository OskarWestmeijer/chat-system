package westmeijer.oskar.server.service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import westmeijer.oskar.server.client.ClientListener;

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
    log.info("Client joined chat. clientIp: {}", socket.getInetAddress());
    ClientListener clientListener = new ClientListener(socket);

    // TODO: think about management over thread pool.
    CONNECTED_CLIENT_CONTROLLERS.add(clientListener);
    Thread thread = new Thread(clientListener);
    thread.start();
    log.info("Connected clients count: {}", CONNECTED_CLIENT_CONTROLLERS.size());
  }

}