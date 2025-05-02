package westmeijer.oskar.client.service;

import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import westmeijer.oskar.client.service.server.ServerListener;
import westmeijer.oskar.client.service.server.ServerProcessor;
import westmeijer.oskar.client.service.terminal.TerminalListener;
import westmeijer.oskar.client.service.terminal.TerminalProcessor;

@Slf4j
public class ClientInitializer {

  public ClientService init() {
    try {
      var socket = InitializerFactory.createSocket("localhost", 5123);
      var input = InitializerFactory.createInput(socket);
      var output = InitializerFactory.createOutput(socket);
      var streamProvider = InitializerFactory.createStreamProvider(socket, input, output);

      var serverMessageProcessor = ServerProcessor.getInstance();
      var serverListener = ServerListener.init(streamProvider, serverMessageProcessor);

      var terminalProcessor = TerminalProcessor.init(streamProvider);
      var terminalListener = TerminalListener.init(streamProvider, terminalProcessor);

      var executor = Executors.newFixedThreadPool(2);
      return ClientService.init(streamProvider, executor, terminalListener, serverListener);
    } catch (Exception e) {
      throw new RuntimeException("Exception during initialization.", e);
    }
  }


}
