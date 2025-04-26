package westmeijer.oskar.client.service;

import java.io.ObjectOutputStream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OutputRequest {

  static void send(ObjectOutputStream outputStream, Object o) {
    try {
      outputStream.writeObject(o);
      outputStream.flush();
    } catch (Exception e) {
      log.error("Exception thrown.", e);
      throw new RuntimeException(e);
    }
  }

}
