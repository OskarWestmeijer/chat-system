package westmeijer.oskar.server.client;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.function.Function;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ClientStreamProvider {

  private final Socket socket;

  private final ObjectInputStream objectInputStream;

  private final ObjectOutputStream objectOutputStream;

  @Getter
  @Setter
  private boolean isConnected = true;

  private static final Function<Closeable, Boolean> streamCloser = o -> {
    try {
      o.close();
      return true;
    } catch (Exception e) {
      return false;
    }
  };

  public void writeToStream(Object o) {
    try {
      objectOutputStream.writeObject(o);
      objectOutputStream.flush();
    } catch (Exception e) {
      log.error("Exception thrown.", e);
      throw new RuntimeException(e);
    }
  }

  public Object readFromStream() {
    try {
      return objectInputStream.readObject();
    } catch (Exception e) {
      log.trace("Exception, while listening for server stream.", e);
      throw new RuntimeException(e);
    }
  }

  public void closeStreams() {
    streamCloser.apply(objectInputStream);
    streamCloser.apply(objectOutputStream);
    streamCloser.apply(socket);
  }

  public static ObjectOutputStream createOutput(Socket socket) {
    try {
      var stream = new ObjectOutputStream(socket.getOutputStream());
      stream.flush();
      return stream;
    } catch (IOException e) {
      log.error("Error creating ObjectOutputStream", e);
      throw new RuntimeException(e);
    }
  }

  public static ObjectInputStream createInput(Socket socket) {
    try {
      return new ObjectInputStream(socket.getInputStream());
    } catch (IOException e) {
      log.error("Error creating ObjectInputStream", e);
      throw new RuntimeException(e);
    }
  }
}
