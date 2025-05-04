package westmeijer.oskar.server.service;

import java.io.IOException;
import java.net.ServerSocket;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import westmeijer.oskar.server.ServerMain;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConnectionListener {

  // TODO: singleton
  private final ServerSocket server;
  private final ConnectionProcessor connectionProcessor;
  private static ConnectionListener instance;

  public static ConnectionListener init(ServerSocket server, ConnectionProcessor processor) {
    if (instance == null) {
      instance = new ConnectionListener(server, processor);
    }
    return instance;
  }

  public static void reset() {
    instance = null;
  }

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

  public static ServerSocket serverSocket(Integer port) {
    try {
      return new ServerSocket(port);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}