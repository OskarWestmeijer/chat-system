package westmeijer.oskar.server;

import westmeijer.oskar.server.service.ConnectionsListener;
import java.io.IOException;

public class ServerMain {

  public static void main(String[] args) throws IOException {
    ConnectionsListener serverController = new ConnectionsListener(5123);
    serverController.listenForConnection();
  }

}
