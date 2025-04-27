package westmeijer.oskar.client.service;


import java.time.Instant;
import java.util.Scanner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import westmeijer.oskar.client.loggers.ServerLogger;
import westmeijer.oskar.shared.model.request.ClientChatRequest;
import westmeijer.oskar.shared.model.request.ClientCommandRequest;
import westmeijer.oskar.shared.model.request.EventType;

@Slf4j
@RequiredArgsConstructor
public class ClientService {

  private final ServerListener serverListener;
  private final Scanner scanner;

  public void start() {
    try {
      serverListener.connect();
      startTerminalScan();
    } catch (Exception e) {
      log.error("Exception received. Disconnecting from server.", e);
    } finally {
      disconnect();
    }
  }

  private void startTerminalScan() {
    try {
      ServerLogger.log("Start chatting. available commands: '/clients', '/history', '/quit'");
      while (serverListener.isConnected()) {
        String userInput = scanner.nextLine();

        switch (userInput) {
          case "/clients" -> sendClientCommandRequest(EventType.LIST_CLIENTS);
          case "/history" -> sendClientCommandRequest(EventType.CHAT_HISTORY);
          case "/quit" -> serverListener.setConnected(false);
          default -> sendClientChatRequest(userInput);
        }
      }
      log.info("No longer connected.");
    } catch (Exception e) {
      log.error("Exception thrown.", e);
      throw new RuntimeException(e);
    }
  }

  private void sendClientChatRequest(String message) {
    ClientChatRequest clientChatRequest = ClientChatRequest.builder()
        .message(message)
        .sendAt(Instant.now())
        .build();
    OutputRequest.send(serverListener.getObjectOutputStream(), clientChatRequest);
  }

  private void sendClientCommandRequest(EventType type) {
    var event = ClientCommandRequest.builder()
        .eventType(type)
        .sendAt(Instant.now())
        .build();
    OutputRequest.send(serverListener.getObjectOutputStream(), event);
  }

  private void disconnect() {
    log.info("Disconnecting.");
    scanner.close();
    serverListener.disconnect();
  }

}
