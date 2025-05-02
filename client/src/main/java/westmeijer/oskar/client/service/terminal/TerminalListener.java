package westmeijer.oskar.client.service.terminal;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import westmeijer.oskar.client.loggers.ServerLogger;
import westmeijer.oskar.client.service.StreamProvider;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TerminalListener {

  private final StreamProvider streamProvider;

  private final TerminalProcessor terminalProcessor;

  private static TerminalListener instance;

  public static TerminalListener init(StreamProvider streamProvider, TerminalProcessor terminalProcessor) {
    if (instance == null) {
      instance = new TerminalListener(streamProvider, terminalProcessor);
    }
    return instance;
  }

  static void reset() {
    instance = null;
  }

  public Runnable runnable() {
    return () -> {
      ServerLogger.log("Start chatting. available commands: '/clients', '/history', '/quit'");
      while (streamProvider.isConnected()) {
        String input = streamProvider.readFromTerminal();
        terminalProcessor.process(input);
      }
      log.info("Outside of terminal loop");
    };
  }

}
