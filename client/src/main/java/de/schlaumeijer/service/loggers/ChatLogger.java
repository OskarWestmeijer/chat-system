package de.schlaumeijer.service.loggers;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatLogger {

  public static void log(String message) {
    log.info(message);
  }

}
