package westmeijer.oskar.client.service;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.function.Function;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import westmeijer.oskar.shared.model.response.ServerMessage;

@Slf4j
public class StreamProvider {

  private final Scanner scanner;

  private final Socket socket;

  private final ObjectInputStream objectInputStream;

  private final ObjectOutputStream objectOutputStream;

  @Getter
  @Setter
  private boolean isConnected = false;

  private static final Function<Closeable, Boolean> streamCloser = o -> {
    try {
      o.close();
      return true;
    } catch (Exception e) {
      return false;
    }
  };

  public StreamProvider(Socket socket, Scanner scanner) {
    this.socket = socket;
    this.objectInputStream = createInput(socket);
    this.objectOutputStream = createOutput(socket);
    this.scanner = scanner;
  }

  public ServerMessage readFromStream() {
    try {
      return (ServerMessage) objectInputStream.readObject();
    } catch (Exception e) {
      log.trace("Exception, while listening for server stream.", e);
      throw new RuntimeException(e);
    }
  }

  public String readFromTerminal() {
    return scanner.nextLine();
  }

  public void writeToStream(Object o) {
    try {
      objectOutputStream.writeObject(o);
      objectOutputStream.flush();
    } catch (Exception e) {
      log.error("Exception thrown.", e);
      throw new RuntimeException(e);
    }
  }

  private ObjectOutputStream createOutput(Socket socket) {
    try {
      return new ObjectOutputStream(socket.getOutputStream());
    } catch (IOException e) {
      log.error("Error creating ObjectOutputStream", e);
      throw new RuntimeException(e);
    }
  }

  private ObjectInputStream createInput(Socket socket) {
    try {
      return new ObjectInputStream(socket.getInputStream());
    } catch (IOException e) {
      log.error("Error creating ObjectInputStream", e);
      throw new RuntimeException(e);
    }
  }

  public void closeStreams() {
    streamCloser.apply(objectInputStream);
    streamCloser.apply(objectOutputStream);
    streamCloser.apply(socket);
    scanner.close();
  }

  public void exit() {
    System.exit(0);
  }

}
