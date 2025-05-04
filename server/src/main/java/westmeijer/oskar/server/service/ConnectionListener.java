package westmeijer.oskar.server.service;

import java.net.ServerSocket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import westmeijer.oskar.server.ServerMain;

@Slf4j
@RequiredArgsConstructor
public class ConnectionListener {

  // TODO: singleton
  private final ServerSocket server;
  private final ConnectionProcessor connectionProcessor;

  public void listenForConnection() {
    log.info("Created chat server. port: {}", server.getLocalPort());
    while (ServerMain.isListening()) {
      try {
        var clientSocket = server.accept();
        connectionProcessor.process(clientSocket);
      } catch (Exception e) {
        log.error("Exception thrown.", e);
      }
    }
  }

}