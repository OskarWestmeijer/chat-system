package westmeijer.oskar.client.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StreamProvider {

  private static StreamProvider instance;

  public static synchronized StreamProvider getInstance() {
    if (StreamProvider.instance == null) {
      StreamProvider.instance = new StreamProvider();
    }
    return StreamProvider.instance;
  }

  public ObjectOutputStream createOutput(Socket socket) {
    try {
      return new ObjectOutputStream(socket.getOutputStream());
    } catch (IOException e) {
      log.error("Error creating ObjectOutputStream", e);
      throw new RuntimeException(e);
    }
  }

  public ObjectInputStream createInput(Socket socket) {
    try {
      return new ObjectInputStream(socket.getInputStream());
    } catch (IOException e) {
      log.error("Error creating ObjectInputStream", e);
      throw new RuntimeException(e);
    }
  }

}
