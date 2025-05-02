package westmeijer.oskar.client.service.terminal;

import static java.util.Objects.requireNonNull;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import westmeijer.oskar.client.service.StreamProvider;
import westmeijer.oskar.shared.model.request.ClientChatRequest;
import westmeijer.oskar.shared.model.request.ClientCommandRequest;
import westmeijer.oskar.shared.model.request.EventType;

@Slf4j
@RequiredArgsConstructor
public class TerminalProcessor {

  private final StreamProvider streamProvider;

  private static TerminalProcessor instance;

  public static TerminalProcessor init(StreamProvider streamProvider) {
    requireNonNull(streamProvider, "streamProvider is required");
    if (instance == null) {
      instance = new TerminalProcessor(streamProvider);
    }
    return instance;
  }

  public void process(String input) {
    switch (input) {
      case "/clients" -> sendClientCommandRequest(EventType.LIST_CLIENTS);
      case "/history" -> sendClientCommandRequest(EventType.CHAT_HISTORY);
      case "/quit" -> quit();
      default -> sendClientChatRequest(input);
    }
  }

  private void quit() {
    streamProvider.setConnected(false);
    throw new RuntimeException("Terminal disconnect.");
  }

  private void sendClientChatRequest(String message) {
    ClientChatRequest clientChatRequest = ClientChatRequest.builder()
        .message(message)
        .sendAt(Instant.now())
        .build();
    streamProvider.writeToStream(clientChatRequest);
  }

  private void sendClientCommandRequest(EventType type) {
    var event = ClientCommandRequest.builder()
        .eventType(type)
        .sendAt(Instant.now())
        .build();
    streamProvider.writeToStream(event);
  }

}
