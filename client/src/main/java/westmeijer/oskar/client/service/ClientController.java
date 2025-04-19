package westmeijer.oskar.client.service;

import westmeijer.oskar.client.loggers.ChatLogger;
import westmeijer.oskar.client.loggers.ServerLogger;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import westmeijer.oskar.shared.model.ChatMessageDto;
import westmeijer.oskar.shared.model.ClientConnectionDto;

@Slf4j
@RequiredArgsConstructor
public class ClientController {

  private static final String SERVER_DISCONNECTION_COMMAND = "/goodbye";

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
      ServerLogger.log("--- Waiting for input ---");
      while (isConnected) {
        String userInput = scanner.nextLine();
        ChatMessageDto chatMessageDto = new ChatMessageDto();
        chatMessageDto.setMessage(userInput);
        objectOutputStream.writeObject(chatMessageDto);
        objectOutputStream.flush();

        if (userInput.equals("/quit")) {
          disconnect();
        }
      }
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
          Object serverMessage = objectInputStream.readObject();
          if (serverMessage != null) {
            processServerMessage(serverMessage);
          }
        }
      } catch (Exception e) {
        log.error("Exception thrown. Shutting down client.", e);
        throw new RuntimeException(e);
      }
    };
    executorService.submit(listenForMessagesTask);
  }

  private void processServerMessage(Object serverMessage) {
    log.trace("Received message: {}", serverMessage);
    if (serverMessage instanceof ChatMessageDto) {
      processChatMessageDto((ChatMessageDto) serverMessage);
    } else if (serverMessage instanceof ClientConnectionDto) {
      processClientConnectionDto((ClientConnectionDto) serverMessage);
    }
  }

  private void processClientConnectionDto(ClientConnectionDto clientConnectionDto) {
    ServerLogger.log(clientConnectionDto.toString());
  }

  private void processChatMessageDto(ChatMessageDto chatMessageDto) {
    if (chatMessageDto.getMessage().equals(SERVER_DISCONNECTION_COMMAND)) {
      ServerLogger.log("---Received disconnection command from server.---");
      disconnect();
    } else {
      ChatLogger.log("%s: %s".formatted(chatMessageDto.getClientConnectionDto().getId(), chatMessageDto.getMessage()));
    }
  }

  private synchronized void disconnect() {
    ServerLogger.log("--- Disconnecting ---");
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
