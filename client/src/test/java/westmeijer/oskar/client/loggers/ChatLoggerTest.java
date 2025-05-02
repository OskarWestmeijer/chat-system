package westmeijer.oskar.client.loggers;

import org.junit.jupiter.api.Test;

class ChatLoggerTest {

  @Test
  void shouldLog() {
    ChatLogger.log("Juhu!");
  }

}