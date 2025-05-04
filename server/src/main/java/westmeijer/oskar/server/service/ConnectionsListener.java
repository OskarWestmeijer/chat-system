package westmeijer.oskar.server.service;

import java.net.ServerSocket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import westmeijer.oskar.server.ServerMain;

@Slf4j
@RequiredArgsConstructor
public class ConnectionsListener {

  // TODO: singleton
  private final ServerSocket server;
  private final ServerConnectionProcessor serverConnectionProcessor;

  public void listenForConnection() {
    log.info("Created chat server. port: {}", server.getLocalPort());
    while (ServerMain.isListening()) {
      try {
        var clientSocket = server.accept();
        serverConnectionProcessor.process(clientSocket);
      } catch (Exception e) {
        log.error("Exception thrown.", e);
      }
    }
  }

}