package westmeijer.oskar.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import westmeijer.oskar.client.service.ClientService;
import westmeijer.oskar.client.service.StreamProvider;
import westmeijer.oskar.client.service.server.ServerListener;
import westmeijer.oskar.client.service.server.ServerProcessor;
import westmeijer.oskar.client.service.terminal.TerminalListener;
import westmeijer.oskar.client.service.terminal.TerminalProcessor;

@Slf4j
public class ClientMain {

  public static void main(String[] args) {
    log.info("Start client application");
    try {
      var socket = new Socket("localhost", 5123);
      var input = createInput(socket);
      var output = createOutput(socket);
      var streamProvider = new StreamProvider(new Scanner(System.in), socket, input, output);

      var serverMessageProcessor = new ServerProcessor();
      var serverListener = new ServerListener(streamProvider, serverMessageProcessor);

      var terminalProcessor = new TerminalProcessor(streamProvider);
      var terminalListener = new TerminalListener(streamProvider, terminalProcessor);

      var executor = Executors.newFixedThreadPool(2);
      var clientService = new ClientService(streamProvider, executor, terminalListener, serverListener);
      clientService.start();
    } catch (Exception e) {
      log.error("Received exception.", e);
    }
    log.info("End client application");
  }

  private static ObjectOutputStream createOutput(Socket socket) {
    try {
      return new ObjectOutputStream(socket.getOutputStream());
    } catch (IOException e) {
      log.error("Error creating ObjectOutputStream", e);
      throw new RuntimeException(e);
    }
  }

  private static ObjectInputStream createInput(Socket socket) {
    try {
      return new ObjectInputStream(socket.getInputStream());
    } catch (IOException e) {
      log.error("Error creating ObjectInputStream", e);
      throw new RuntimeException(e);
    }
  }

}
