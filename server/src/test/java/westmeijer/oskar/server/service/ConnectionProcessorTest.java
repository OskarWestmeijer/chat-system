package westmeijer.oskar.server.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import java.net.Socket;
import java.util.List;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.MockedStatic;
import westmeijer.oskar.server.client.ClientListener;
import westmeijer.oskar.server.service.model.ClientActivity;
import westmeijer.oskar.server.service.model.ClientDetails;
import westmeijer.oskar.shared.model.response.RelayedClientActivity.ActivityType;

class ConnectionProcessorTest {

  @BeforeEach
  void resetSingleton() throws Exception {
    var instanceField = ConnectionProcessor.class.getDeclaredField("instance");
    instanceField.setAccessible(true);
    instanceField.set(null, null);
  }

  @Test
  void shouldCreateNewInstanceWhenNull() {
    var historizedEventService = mock(HistorizedEventService.class);
    var clientInitializer = mock(ClientInitializer.class);
    var clientRegister = mock(ClientRegister.class);

    var result = ConnectionProcessor.init(historizedEventService, clientInitializer, clientRegister);

    BDDAssertions.then(result).isNotNull();
  }

  @Test
  void shouldReturnSameInstanceIfAlreadyInitialized() {
    var historizedEventService = mock(HistorizedEventService.class);
    var clientInitializer = mock(ClientInitializer.class);
    var clientRegister = mock(ClientRegister.class);

    var first = ConnectionProcessor.init(historizedEventService, clientInitializer, clientRegister);
    var second = ConnectionProcessor.init(mock(HistorizedEventService.class), mock(ClientInitializer.class), mock(ClientRegister.class));

    BDDAssertions.then(second).isSameAs(first);
  }

  @Test
  void shouldProcessClientConnection() {
    try (MockedStatic<OutgoingNotificationService> notificationMock = mockStatic(OutgoingNotificationService.class)) {
      var historizedEventService = mock(HistorizedEventService.class);
      var clientInitializer = mock(ClientInitializer.class);
      var clientRegister = mock(ClientRegister.class);
      var clientListener = mock(ClientListener.class);
      var clientDetails = mock(ClientDetails.class);
      var socket = mock(Socket.class);

      given(clientInitializer.init(socket, historizedEventService, clientRegister)).willReturn(clientListener);
      lenient().when(clientRegister.registerClient(clientListener)).thenReturn(clientListener);
      given(clientListener.getClientDetails()).willReturn(clientDetails);

      doNothing().when(historizedEventService).recordMessage(any(ClientActivity.class));
      given(clientRegister.getClients()).willReturn(List.of(clientListener));

      // when
      var connectionProcessor = ConnectionProcessor.init(historizedEventService, clientInitializer, clientRegister);
      connectionProcessor.process(socket);

      // then
      BDDMockito.then(clientRegister).should().registerClient(clientListener);
      BDDMockito.then(historizedEventService).should().recordMessage(any(ClientActivity.class));
      notificationMock.verify(() -> OutgoingNotificationService.notifyClientActivity(any(), any(), eq(ActivityType.CONNECTED)));
    }
  }

}