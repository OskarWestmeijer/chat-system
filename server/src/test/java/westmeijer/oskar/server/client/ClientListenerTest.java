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
    // Arrange
    var historizedEventService = mock(HistorizedEventService.class);
    var clientRegister = mock(ClientRegister.class);
    var clientStreamProvider = mock(ClientStreamProvider.class);
    var clientProcessor = mock(ClientProcessor.class);
    var clientDetails = mock(ClientDetails.class);

    // Create the ClientListener instance
    var clientListener = new ClientListener(historizedEventService, clientRegister, clientStreamProvider, clientProcessor, clientDetails);

    // Mock stream provider behavior
    when(clientStreamProvider.isConnected()).thenReturn(true, false); // Simulate one message read, then disconnect
    when(clientStreamProvider.readFromStream()).thenReturn("Message");

    // Run the ClientListener in a separate thread to simulate asynchronous processing
    Thread listenerThread = new Thread(clientListener);
    listenerThread.start();

    // Simulate stopping the client after the first message
    given(clientStreamProvider.readFromStream()).willReturn("Message");

    // Simulate disconnecting
    listenerThread.join(); // Wait for the listener to finish processing

    // Verify interactions during disconnect
    verify(clientStreamProvider).closeStreams();
    verify(clientRegister).unregisterClient(clientListener);
    verify(historizedEventService).recordMessage(any(ClientActivity.class));
    verify(clientRegister).getClients();
  }

  @Test
  @SneakyThrows
  void shouldHandleExceptionDuringMessageProcessing() {
    // Arrange
    var historizedEventService = mock(HistorizedEventService.class);
    var clientRegister = mock(ClientRegister.class);
    var clientStreamProvider = mock(ClientStreamProvider.class);
    var clientProcessor = mock(ClientProcessor.class);
    var clientDetails = mock(ClientDetails.class);

    // Create the ClientListener instance
    var clientListener = new ClientListener(historizedEventService, clientRegister, clientStreamProvider, clientProcessor, clientDetails);

    // Mock stream provider to simulate an exception while reading the message
    when(clientStreamProvider.isConnected()).thenReturn(true);
    when(clientStreamProvider.readFromStream()).thenThrow(new RuntimeException("Test Exception"));

    // Run the ClientListener in a separate thread to simulate asynchronous processing
    Thread listenerThread = new Thread(clientListener);
    listenerThread.start();

    // Wait for the listener to handle the exception and disconnect
    listenerThread.join();

    // Verify interactions during exception handling
    verify(clientStreamProvider).readFromStream();
    verify(clientStreamProvider).closeStreams();
    verify(clientRegister).unregisterClient(clientListener);
    verify(historizedEventService).recordMessage(any(ClientActivity.class));
    verify(clientRegister).getClients();
  }
}
