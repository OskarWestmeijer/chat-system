package westmeijer.oskar.client.service.server;

import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.mockStatic;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import westmeijer.oskar.client.loggers.ChatLogger;
import westmeijer.oskar.client.loggers.ServerLogger;
import westmeijer.oskar.shared.model.response.ChatHistoryResponse;
import westmeijer.oskar.shared.model.response.ClientListResponse;
import westmeijer.oskar.shared.model.response.RelayedChatMessage;
import westmeijer.oskar.shared.model.response.RelayedClientActivity;
import westmeijer.oskar.shared.model.response.ServerMessage;

@ExtendWith(MockitoExtension.class)
class ServerProcessorTest {

  private final ServerProcessor serverProcessor = ServerProcessor.getInstance();

  @Test
  void shouldProcessChatHistoryResponse() {
    // Given
    ChatHistoryResponse response = mock(ChatHistoryResponse.class);
    given(response.getMessageHistory()).willReturn(List.of("Message 1", "Message 2"));

    try (MockedStatic<ServerLogger> serverLoggerMock = mockStatic(ServerLogger.class)) {
      // When
      serverProcessor.process(response);

      // Then
      serverLoggerMock.verify(() -> ServerLogger.log("-- START OF HISTORY --"));
      serverLoggerMock.verify(() -> ServerLogger.log("Message 1"));
      serverLoggerMock.verify(() -> ServerLogger.log("Message 2"));
      serverLoggerMock.verify(() -> ServerLogger.log("-- END OF HISTORY --"));
      serverLoggerMock.verify(() -> ServerLogger.log(""));
    }
  }

  @Test
  void shouldProcessClientListResponse() {
    // Given
    ClientListResponse response = mock(ClientListResponse.class);
    given(response.getClients()).willReturn(List.of("Client A", "Client B"));

    try (MockedStatic<ServerLogger> serverLoggerMock = mockStatic(ServerLogger.class)) {
      // When
      serverProcessor.process(response);

      // Then
      serverLoggerMock.verify(() -> ServerLogger.log("-- START OF CLIENT LIST --"));
      serverLoggerMock.verify(() -> ServerLogger.log("Client A"));
      serverLoggerMock.verify(() -> ServerLogger.log("Client B"));
      serverLoggerMock.verify(() -> ServerLogger.log("-- END OF CLIENT LIST --"));
      serverLoggerMock.verify(() -> ServerLogger.log(""));
    }
  }

  @Test
  void shouldProcessRelayedChatMessage() {
    // Given
    RelayedChatMessage message = mock(RelayedChatMessage.class);
    given(message.getClientLog()).willReturn("Chat log");

    try (MockedStatic<ChatLogger> chatLoggerMock = mockStatic(ChatLogger.class)) {
      // When
      serverProcessor.process(message);

      // Then
      chatLoggerMock.verify(() -> ChatLogger.log("Chat log"));
    }
  }

  @Test
  void shouldProcessRelayedClientActivity() {
    // Given
    RelayedClientActivity activity = mock(RelayedClientActivity.class);
    given(activity.getClientLog()).willReturn("Activity log");

    try (MockedStatic<ServerLogger> serverLoggerMock = mockStatic(ServerLogger.class)) {
      // When
      serverProcessor.process(activity);

      // Then
      serverLoggerMock.verify(() -> ServerLogger.log("Activity log"));
    }
  }

  @Test
  void shouldThrowWhenUnknownMessageType() {
    // Given
    ServerMessage unknownMessage = mock(ServerMessage.class);

    // When / Then
    thenThrownBy(() -> serverProcessor.process(unknownMessage))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Could not process received message");
  }
}

