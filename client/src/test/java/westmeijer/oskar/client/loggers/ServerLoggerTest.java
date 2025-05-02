package westmeijer.oskar.client.loggers;

import org.junit.jupiter.api.Test;

class ServerLoggerTest {

  @Test
  void shouldLog() {
    ServerLogger.log("Hey buddy!");
  }

}