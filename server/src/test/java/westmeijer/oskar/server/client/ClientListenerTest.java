package westmeijer.oskar.server.client;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import westmeijer.oskar.server.service.ClientRegister;
import westmeijer.oskar.server.service.HistorizedEventService;
import westmeijer.oskar.server.service.model.ClientActivity;
import westmeijer.oskar.server.service.model.ClientDetails;

class ClientListenerTest {

  @Test
  @SneakyThrows
  void shouldProcessMessageAndDisconnectClient() {
    var historizedEventService = mock(HistorizedEventService.class);
    var clientRegister = mock(ClientRegister.class);
    var clientStreamProvider = mock(ClientStreamProvider.class);
    var clientProcessor = mock(ClientProcessor.class);
    var clientDetails = mock(ClientDetails.class);

    var clientListener = new ClientListener(historizedEventService, clientRegister, clientStreamProvider, clientProcessor, clientDetails);

    when(clientStreamProvider.isConnected()).thenReturn(true, false);
    when(clientStreamProvider.readFromStream()).thenReturn("Message");

    given(clientStreamProvider.readFromStream()).willReturn("Message");

    clientListener.run();

    verify(clientStreamProvider).closeStreams();
    verify(clientRegister).unregisterClient(clientListener);
    verify(historizedEventService).recordMessage(any(ClientActivity.class));
    verify(clientRegister).getClients();
  }

  @Test
  @SneakyThrows
  void shouldHandleExceptionDuringMessageProcessing() {
    var historizedEventService = mock(HistorizedEventService.class);
    var clientRegister = mock(ClientRegister.class);
    var clientStreamProvider = mock(ClientStreamProvider.class);
    var clientProcessor = mock(ClientProcessor.class);
    var clientDetails = mock(ClientDetails.class);

    var clientListener = new ClientListener(historizedEventService, clientRegister, clientStreamProvider, clientProcessor, clientDetails);

    when(clientStreamProvider.isConnected()).thenReturn(true);
    when(clientStreamProvider.readFromStream()).thenThrow(new RuntimeException("Test Exception"));

    clientListener.run();

    verify(clientStreamProvider).readFromStream();
    verify(clientStreamProvider).closeStreams();
    verify(clientRegister).unregisterClient(clientListener);
    verify(historizedEventService).recordMessage(any(ClientActivity.class));
    verify(clientRegister).getClients();
  }
}
