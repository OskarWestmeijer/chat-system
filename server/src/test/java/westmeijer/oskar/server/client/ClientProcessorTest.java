package westmeijer.oskar.server.client;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import westmeijer.oskar.server.service.ClientRegister;
import westmeijer.oskar.server.service.HistorizedEventService;
import westmeijer.oskar.server.service.OutgoingNotificationService;
import westmeijer.oskar.server.service.model.ClientActivity;
import westmeijer.oskar.server.service.model.ClientDetails;
import westmeijer.oskar.server.service.model.ClientMessage;
import westmeijer.oskar.server.service.model.HistorizedEventType;
import westmeijer.oskar.shared.model.request.ClientChatRequest;
import westmeijer.oskar.shared.model.request.ClientCommandRequest;
import westmeijer.oskar.shared.model.request.EventType;
import westmeijer.oskar.shared.model.response.ChatHistoryResponse;
import westmeijer.oskar.shared.model.response.ClientListResponse;

class ClientProcessorTest {

  @Test
  void shouldProcessClientChatRequest() {
    var history = mock(HistorizedEventService.class);
    var register = mock(ClientRegister.class);
    var details = mock(ClientDetails.class);
    var processor = new ClientProcessor(history, register, details);
    var request = ClientChatRequest.builder()
        .message("hello")
        .sendAt(Instant.now())
        .build();

    try (MockedStatic<OutgoingNotificationService> outgoingMock = mockStatic(OutgoingNotificationService.class)) {
      processor.processMessage(request);

      // verify message recorded
      then(history).hasNoNullFieldsOrProperties(); // optional
      then(history).satisfies(h -> org.mockito.Mockito.verify(h).recordMessage(any(ClientMessage.class)));
      // verify notification sent
      outgoingMock.verify(() -> OutgoingNotificationService.notifyChatMessage(anyList(), any(), any()));
    }
  }

  @Test
  void shouldProcessClientCommandRequest_listClients() {
    var history = mock(HistorizedEventService.class);
    var register = mock(ClientRegister.class);
    var details = mock(ClientDetails.class);
    var listener = mock(ClientListener.class);

    given(register.getClient(details)).willReturn(Optional.of(listener));
    given(register.getClients()).willReturn(List.of(listener));
    given(listener.getClientDetails()).willReturn(details);
    given(details.getClientLog()).willReturn("log");

    var processor = new ClientProcessor(history, register, details);
    var request = ClientCommandRequest.builder()
        .eventType(EventType.LIST_CLIENTS)
        .build();

    try (MockedStatic<OutgoingNotificationService> outgoingMock = mockStatic(OutgoingNotificationService.class)) {
      processor.processMessage(request);

      outgoingMock.verify(() -> OutgoingNotificationService
          .sendMessage(eq(listener), any(ClientListResponse.class)));
    }
  }

  @Test
  void shouldProcessClientCommandRequest_chatHistory() {
    var history = mock(HistorizedEventService.class);
    var register = mock(ClientRegister.class);
    var details = mock(ClientDetails.class);
    var listener = mock(ClientListener.class);

    var event = new ClientActivity(HistorizedEventType.CLIENT_CONNECTED, details);
    given(history.getHistory()).willReturn(List.of(event));
    given(event.getHistorizedLog()).willReturn("log");
    given(register.getClient(details)).willReturn(Optional.of(listener));

    var processor = new ClientProcessor(history, register, details);
    var request = ClientCommandRequest.builder()
        .eventType(EventType.CHAT_HISTORY)
        .build();

    try (MockedStatic<OutgoingNotificationService> outgoingMock = mockStatic(OutgoingNotificationService.class)) {
      processor.processMessage(request);

      outgoingMock.verify(() -> OutgoingNotificationService
          .sendMessage(eq(listener), any(ChatHistoryResponse.class)));
    }
  }

  @Test
  void shouldThrowForUnknownMessageType() {
    var processor = new ClientProcessor(mock(HistorizedEventService.class), mock(ClientRegister.class), mock(ClientDetails.class));

    var unknownMessage = new Object();

    org.assertj.core.api.Assertions.assertThatThrownBy(() -> processor.processMessage(unknownMessage))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Did not find processing path");
  }
}
