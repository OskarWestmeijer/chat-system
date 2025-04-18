package de.schlaumeijer.bl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerCtrl {

  private final ServerSocket server;

  public static final List<ClientCtrl> CONNECTED_CLIENT_CTRLS = new ArrayList<>();

  public ServerCtrl(int port) throws IOException {
    this.server = new ServerSocket(port);
    log.info("Created chat server. port: {}, connected clients count: {}", port, CONNECTED_CLIENT_CTRLS.size());
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
    ClientCtrl clientCtrl = new ClientCtrl(socket);
    CONNECTED_CLIENT_CTRLS.add(clientCtrl);
    Thread thread = new Thread(clientCtrl);
    thread.start();
  }

}