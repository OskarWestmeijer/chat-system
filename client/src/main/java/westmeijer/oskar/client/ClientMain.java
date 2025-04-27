package westmeijer.oskar.client;

import java.net.Socket;
import java.util.Scanner;
import lombok.extern.slf4j.Slf4j;
import westmeijer.oskar.client.service.ClientService;
import westmeijer.oskar.client.service.ServerListener;
import westmeijer.oskar.client.service.StreamProvider;

@Slf4j
public class ClientMain {

  public static void main(String[] args) {
    log.info("Start client application");
    try {
      var socket = new Socket("localhost", 5123);
      var output = StreamProvider.getInstance().createOutput(socket);
      var input = StreamProvider.getInstance().createInput(socket);
      ServerListener serverListener = new ServerListener(socket, input, output);
      ClientService client = new ClientService(serverListener, new Scanner(System.in));
      client.start();
    } catch (Exception e) {
      log.error("Received exception.", e);
    }
    log.info("End client application");
  }

}
