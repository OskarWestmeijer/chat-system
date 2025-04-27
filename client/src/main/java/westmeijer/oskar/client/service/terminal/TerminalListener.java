package westmeijer.oskar.client.service.terminal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import westmeijer.oskar.client.loggers.ServerLogger;
import westmeijer.oskar.client.service.StreamProvider;

@Slf4j
@RequiredArgsConstructor
public class TerminalListener {

  private final StreamProvider streamProvider;

  private final TerminalProcessor terminalProcessor;

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
