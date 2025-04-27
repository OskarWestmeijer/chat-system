package westmeijer.oskar.client.service.terminal;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import westmeijer.oskar.client.service.StreamProvider;
import westmeijer.oskar.shared.model.request.ClientChatRequest;
import westmeijer.oskar.shared.model.request.ClientCommandRequest;
import westmeijer.oskar.shared.model.request.EventType;

@ExtendWith(MockitoExtension.class)
class TerminalProcessorTest {

  @Mock
  private StreamProvider streamProvider;

  private TerminalProcessor terminalProcessor;

  @BeforeEach
  void setUp() {
    terminalProcessor = new TerminalProcessor(streamProvider);
  }

  @Test
  void shouldSendClientListRequestOnClientsCommand() {
    // When
    terminalProcessor.process("/clients");

    // Then
    ArgumentCaptor<ClientCommandRequest> captor = ArgumentCaptor.forClass(ClientCommandRequest.class);
    BDDMockito.then(streamProvider).should().writeToStream(captor.capture());

    ClientCommandRequest sentRequest = captor.getValue();
    then(sentRequest.getEventType()).isEqualTo(EventType.LIST_CLIENTS);
    then(sentRequest.getSendAt()).isBeforeOrEqualTo(Instant.now());
  }

  @Test
  void shouldSendChatHistoryRequestOnHistoryCommand() {
    // When
    terminalProcessor.process("/history");

    // Then
    ArgumentCaptor<ClientCommandRequest> captor = ArgumentCaptor.forClass(ClientCommandRequest.class);
    BDDMockito.then(streamProvider).should().writeToStream(captor.capture());

    ClientCommandRequest sentRequest = captor.getValue();
    then(sentRequest.getEventType()).isEqualTo(EventType.CHAT_HISTORY);
    then(sentRequest.getSendAt()).isBeforeOrEqualTo(Instant.now());
  }

  @Test
  void shouldSendClientChatRequestOnNormalMessage() {
    // Given
    String message = "Hello World";

    // When
    terminalProcessor.process(message);

    // Then
    ArgumentCaptor<ClientChatRequest> captor = ArgumentCaptor.forClass(ClientChatRequest.class);
    BDDMockito.then(streamProvider).should().writeToStream(captor.capture());

    ClientChatRequest sentRequest = captor.getValue();
    then(sentRequest.getMessage()).isEqualTo(message);
    then(sentRequest.getSendAt()).isBeforeOrEqualTo(Instant.now());
  }

  @Test
  void shouldSetDisconnectedAndThrowOnQuitCommand() {
    // When / Then
    thenThrownBy(() -> terminalProcessor.process("/quit"))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Terminal disconnect.");

    BDDMockito.then(streamProvider).should().setConnected(false);
  }
}
