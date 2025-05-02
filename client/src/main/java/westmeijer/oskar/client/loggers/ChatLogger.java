package westmeijer.oskar.client.loggers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatLogger {

  public static void log(String message) {
    log.info(message);
  }

}
