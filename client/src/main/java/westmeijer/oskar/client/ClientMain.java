package westmeijer.oskar.client;

import lombok.extern.slf4j.Slf4j;
import westmeijer.oskar.client.service.ClientInitializer;

@Slf4j
public class ClientMain {

  public static void main(String[] args) {
    new ClientInitializer().init().start();
  }

}
