package westmeijer.oskar.client;

import java.util.Scanner;
import lombok.extern.slf4j.Slf4j;
import westmeijer.oskar.client.service.ClientService;
import westmeijer.oskar.client.service.ServerListener;

@Slf4j
public class ClientMain {

  public static void main(String[] args) {
    ServerListener serverListener = new ServerListener("localhost", 5123);
    ClientService client = new ClientService(serverListener, new Scanner(System.in));
    client.start();
  }

}
