package de.schlaumeijer;

import de.schlaumeijer.bl.ServerCtrl;
import java.io.IOException;

public class Main {

  public static void main(String[] args) throws IOException {
    ServerCtrl serverController = new ServerCtrl(5123);
    serverController.listenForConnection();
  }

}
