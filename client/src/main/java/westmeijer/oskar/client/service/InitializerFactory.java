package westmeijer.oskar.client.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InitializerFactory {

  static Socket createSocket(String host, Integer port) {
    try {
      return new Socket(host, port);
    } catch (IOException e) {
      log.error("Error creating Socket", e);
      throw new RuntimeException(e);
    }
  }

  static ObjectOutputStream createOutput(Socket socket) {
    try {
      return new ObjectOutputStream(socket.getOutputStream());
    } catch (IOException e) {
      log.error("Error creating ObjectOutputStream", e);
      throw new RuntimeException(e);
    }
  }

  static ObjectInputStream createInput(Socket socket) {
    try {
      return new ObjectInputStream(socket.getInputStream());
    } catch (IOException e) {
      log.error("Error creating ObjectInputStream", e);
      throw new RuntimeException(e);
    }
  }

  static StreamProvider createStreamProvider(Socket socket, ObjectInputStream input, ObjectOutputStream output) {
    return new StreamProvider(new Scanner(System.in), socket, input, output);
  }

}
