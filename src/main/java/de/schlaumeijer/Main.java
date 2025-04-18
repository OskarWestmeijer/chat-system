package de.schlaumeijer;

import de.schlaumeijer.service.ConnectionsListener;
import java.io.IOException;

public class Main {

  public static void main(String[] args) throws IOException {
    ConnectionsListener serverController = new ConnectionsListener(5123);
    serverController.listenForConnection();
  }

}
