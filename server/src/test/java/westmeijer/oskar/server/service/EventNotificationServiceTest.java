package westmeijer.oskar.server.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.willThrow;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import westmeijer.oskar.server.client.ClientListener;
import westmeijer.oskar.server.service.model.ClientActivity;
import westmeijer.oskar.server.service.model.ClientDetails;
import westmeijer.oskar.server.service.model.ClientMessage;
import westmeijer.oskar.server.service.model.HistorizedEventType;
import westmeijer.oskar.shared.model.response.RelayedChatMessage;
import westmeijer.oskar.shared.model.response.RelayedClientActivity;
import westmeijer.oskar.shared.model.response.RelayedClientActivity.ActivityType;
import westmeijer.oskar.shared.model.response.ServerMessage;

class EventNotificationServiceTest {

  private ClientListener client;
  private ObjectOutputStream outputStream;

  @BeforeEach
  void setUp() {
    client = mock(ClientListener.class);
    outputStream = mock(ObjectOutputStream.class);

    given(client.getObjectOutputStream()).willReturn(outputStream);
  }

  @Test
  @SneakyThrows
  void shouldSendMessage() {
    var serverMessage = mock(ServerMessage.class);

    EventNotificationService.sendMessage(client, serverMessage);

    BDDMockito.then(outputStream).should().writeObject(serverMessage);
    BDDMockito.then(outputStream).should().flush();
  }

  @Test
  @SneakyThrows
  void shouldNotThrowExceptionOnFailure() {
    var serverMessage = mock(ServerMessage.class);
    willThrow(new IOException("Write failed")).given(outputStream).writeObject(serverMessage);

    assertDoesNotThrow(() -> EventNotificationService.sendMessage(client, serverMessage));

    BDDMockito.then(outputStream).should().writeObject(serverMessage);
    BDDMockito.then(outputStream).shouldHaveNoMoreInteractions();
  }

  @Test
  @SneakyThrows
  void shouldNotifyClientActivity() {
    // given
    var clientDetails = ClientDetails.from("#123", "1234");
    var clientActivity = new ClientActivity(HistorizedEventType.CLIENT_CONNECTED, clientDetails);
    var clients = List.of(client);

    // when
    EventNotificationService.notifyClientActivity(clients, clientActivity, ActivityType.CONNECTED);

    // then
    BDDMockito.then(outputStream).should().writeObject(any(RelayedClientActivity.class));
    BDDMockito.then(outputStream).should().flush();
  }

  @Test
  void shouldNotifyChatMessage() throws IOException {
    // given
    var clientDetails = ClientDetails.from("#123", "1234");
    var message = mock(ClientMessage.class);
    given(message.getMessage()).willReturn("Hello world");
    var audience = List.of(client);

    // when
    EventNotificationService.notifyChatMessage(audience, clientDetails, message);

    // then
    BDDMockito.then(outputStream).should().writeObject(any(RelayedChatMessage.class));
    BDDMockito.then(outputStream).should().flush();
  }

}
