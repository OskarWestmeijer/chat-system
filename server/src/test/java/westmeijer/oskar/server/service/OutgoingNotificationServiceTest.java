package westmeijer.oskar.server.service;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;

import java.io.IOException;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import westmeijer.oskar.server.client.ClientListener;
import westmeijer.oskar.server.client.ClientStreamProvider;
import westmeijer.oskar.server.service.model.ClientActivity;
import westmeijer.oskar.server.service.model.ClientDetails;
import westmeijer.oskar.server.service.model.ClientMessage;
import westmeijer.oskar.server.service.model.HistorizedEventType;
import westmeijer.oskar.shared.model.response.RelayedChatMessage;
import westmeijer.oskar.shared.model.response.RelayedClientActivity;
import westmeijer.oskar.shared.model.response.RelayedClientActivity.ActivityType;
import westmeijer.oskar.shared.model.response.ServerMessage;

class OutgoingNotificationServiceTest {

  private ClientListener client;
  private ClientStreamProvider provider;

  @BeforeEach
  void setUp() {
    client = mock(ClientListener.class);
    provider = mock(ClientStreamProvider.class);

    given(client.getClientStreamProvider()).willReturn(provider);
  }

  @Test
  @SneakyThrows
  void shouldSendMessage() {
    var serverMessage = mock(ServerMessage.class);

    OutgoingNotificationService.sendMessage(client, serverMessage);

    BDDMockito.then(client).should().getClientStreamProvider();
    BDDMockito.then(provider).should().writeToStream(serverMessage);
  }

  @Test
  @SneakyThrows
  void shouldNotifyClientActivity() {
    // given
    var clientDetails = ClientDetails.from("1234");
    var clientActivity = new ClientActivity(HistorizedEventType.CLIENT_CONNECTED, clientDetails);
    var clients = List.of(client);

    // when
    OutgoingNotificationService.notifyClientActivity(clients, clientActivity, ActivityType.CONNECTED);

    // then
    BDDMockito.then(client).should().getClientStreamProvider();
    BDDMockito.then(provider).should().writeToStream(any(RelayedClientActivity.class));
  }

  @Test
  void shouldNotifyChatMessage() throws IOException {
    // given
    var clientDetails = ClientDetails.from("1234");
    var message = mock(ClientMessage.class);
    given(message.getMessage()).willReturn("Hello world");
    var audience = List.of(client);

    // when
    OutgoingNotificationService.notifyChatMessage(audience, clientDetails, message);

    // then
    BDDMockito.then(client).should().getClientStreamProvider();
    BDDMockito.then(provider).should().writeToStream(any(RelayedChatMessage.class));
  }

}
