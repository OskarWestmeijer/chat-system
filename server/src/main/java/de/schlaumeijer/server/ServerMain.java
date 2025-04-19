package de.schlaumeijer.server;

import de.schlaumeijer.server.service.ConnectionsListener;
import java.io.IOException;

public class ServerMain {

  public static void main(String[] args) throws IOException {
    ConnectionsListener serverController = new ConnectionsListener(5123);
    serverController.listenForConnection();
  }

}
