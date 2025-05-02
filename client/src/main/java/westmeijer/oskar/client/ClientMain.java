package westmeijer.oskar.client;

import lombok.extern.slf4j.Slf4j;
import westmeijer.oskar.client.service.ClientInitializer;

@Slf4j
public class ClientMain {

  public static void main(String[] args) {
    log.info("Start client application");
    try {
      var initializer = new ClientInitializer();
      var clientService = initializer.init();
      clientService.start();
    } catch (Exception e) {
      log.error("Received exception.", e);
    }
    log.info("End client application");
  }

}
