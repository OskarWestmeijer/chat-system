package westmeijer.oskar.client.loggers;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerLogger {

  public static void log(String message) {
    log.info(message);
  }

}
