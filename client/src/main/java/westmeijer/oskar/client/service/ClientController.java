package westmeijer.oskar.client.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.time.Instant;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import westmeijer.oskar.client.loggers.ChatLogger;
import westmeijer.oskar.client.loggers.ServerLogger;
import westmeijer.oskar.shared.model.request.ClientChatRequest;
import westmeijer.oskar.shared.model.request.ClientCommandRequest;
import westmeijer.oskar.shared.model.request.EventType;
import westmeijer.oskar.shared.model.response.ChatHistoryResponse;
import westmeijer.oskar.shared.model.response.ClientListResponse;
import westmeijer.oskar.shared.model.response.RelayedChatMessage;
import westmeijer.oskar.shared.model.response.RelayedClientActivity;
import westmeijer.oskar.shared.model.response.ServerMessage;

@Slf4j
@RequiredArgsConstructor
public class ClientController {

  private boolean isConnected = false;

  private final String serverIp;

  private final int serverPort;

  private Socket socket;

  private InputStream inputStream;

  private ObjectInputStream objectInputStream;

  private OutputStream outputStream;

  private ObjectOutputStream objectOutputStream;

  private Scanner scanner;

  private ExecutorService executorService;

  public void connect() {
    try {
      this.socket = new Socket(serverIp, serverPort);
      this.isConnected = true;
      System.out.println("Connected to Server at " + serverIp + " " + serverPort);
      outputStream = socket.getOutputStream();
      objectOutputStream = new ObjectOutputStream(outputStream);
      objectOutputStream.flush();
      inputStream = socket.getInputStream();
      objectInputStream = new ObjectInputStream(inputStream);
      scanner = new Scanner(System.in);
      listenForMessagesLoop();
      writeMessageLoop();
    } catch (IOException e) {
      log.error("Error while connecting to server.", e);
      throw new RuntimeException(e);
    }
  }

  private void writeMessageLoop() {
    try {
      ServerLogger.log("Start chatting. available commands: '/clients', '/history', '/quit'");
      while (isConnected) {
        String userInput = scanner.nextLine();

        switch (userInput) {
          case "/clients" -> sendClientRequest(EventType.LIST_CLIENTS);
          case "/history" -> sendClientRequest(EventType.CHAT_HISTORY);
          case "/quit" -> disconnect();
          default -> sendClientChatRequest(userInput);
        }
      }
    } catch (Exception e) {
      log.error("Exception thrown.", e);
      throw new RuntimeException(e);
    }
  }

  private void sendClientChatRequest(String message) {
    try {
      ClientChatRequest clientChatRequest = ClientChatRequest.builder()
          .message(message)
          .sendAt(Instant.now())
          .build();
      objectOutputStream.writeObject(clientChatRequest);
      objectOutputStream.flush();
    } catch (Exception e) {
      log.error("Exception thrown.", e);
      throw new RuntimeException(e);
    }
  }

  private void sendClientRequest(EventType type) {
    try {
      var event = ClientCommandRequest.builder()
          .eventType(type)
          .sendAt(Instant.now())
          .build();
      objectOutputStream.writeObject(event);
      objectOutputStream.flush();
    } catch (Exception e) {
      log.error("Exception thrown.", e);
      throw new RuntimeException(e);
    }
  }

  private void listenForMessagesLoop() {
    executorService = Executors.newSingleThreadExecutor();

    // TODO: when server shuts down, this application does not
    Runnable listenForMessagesTask = () -> {
      Thread.currentThread().setName("Listener");
      try {
        while (isConnected) {
          ServerMessage serverMessage = (ServerMessage) objectInputStream.readObject();
          processReceivedMessage(serverMessage);
        }
      } catch (Exception e) {
        log.error("Exception thrown. Shutting down client.", e);
        disconnect();
        throw new RuntimeException(e);
      }
    };
    executorService.submit(listenForMessagesTask);
  }

  private void processReceivedMessage(ServerMessage message) {
    log.trace("Received message: {}", message);
    switch (message) {
      case ChatHistoryResponse chatHistoryResponse -> processChatHistoryResponse(chatHistoryResponse);
      case ClientListResponse clientListResponse -> processClientListResponse(clientListResponse);
      case RelayedChatMessage relayedChatMessage -> processClientMessage(relayedChatMessage);
      case RelayedClientActivity relayedClientActivity -> processClientActivity(relayedClientActivity);
      default -> throw new RuntimeException("Could not process received message. %s".formatted(message));
    }
  }

  private void processChatHistoryResponse(ChatHistoryResponse message) {
    ServerLogger.log("-- START OF HISTORY --");
    message.getMessageHistory().forEach(ServerLogger::log);
    ServerLogger.log("-- END OF HISTORY --");
    ServerLogger.log("");
  }

  private void processClientListResponse(ClientListResponse clientListResponse) {
    ServerLogger.log("-- START OF CLIENT LIST --");
    clientListResponse.getClients().forEach(ServerLogger::log);
    ServerLogger.log("-- END OF CLIENT LIST --");
    ServerLogger.log("");
  }

  private void processClientMessage(RelayedChatMessage message) {
    ChatLogger.log(message.getClientLog());
  }

  private void processClientActivity(RelayedClientActivity message) {
    ServerLogger.log(message.getClientLog());
  }

  private synchronized void disconnect() {
    ServerLogger.log("Disconnecting");
    try {
      isConnected = false;
      objectInputStream.close();
      objectOutputStream.close();
      outputStream.close();
      inputStream.close();
      scanner.close();
      socket.close();
      System.exit(0);
    } catch (IOException e) {
      log.error("Exception thrown, while disconnecting.", e);
      throw new RuntimeException(e);
    }
  }

}
