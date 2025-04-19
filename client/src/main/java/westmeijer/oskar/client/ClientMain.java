package westmeijer.oskar.client;

import westmeijer.oskar.client.service.ClientController;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientMain {

  public static void main(String[] args) {
    log.info("Starting client application.");
    String connectionServer = "185.239.236.8";
    String connectionLocal = "localhost";

    ClientController client = new ClientController(connectionLocal, 5123);
    client.connect();
  }

}
