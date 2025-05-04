package westmeijer.oskar.server;

import westmeijer.oskar.server.service.ServerInitializer;

public class ServerMain {

  public static void main(String[] args) {
    ServerInitializer.getInstance().init(5123);
  }

  public static boolean isListening() {
    return true;
  }

}
