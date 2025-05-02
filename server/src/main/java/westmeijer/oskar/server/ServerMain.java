package westmeijer.oskar.server;

import java.io.IOException;
import java.net.ServerSocket;
import westmeijer.oskar.server.service.ConnectionsListener;

public class ServerMain {

  public static void main(String[] args) throws IOException {
    ConnectionsListener serverController = new ConnectionsListener(new ServerSocket(5123));
    serverController.listenForConnection();
  }

  public static boolean isListening() {
    return true;
  }

}
