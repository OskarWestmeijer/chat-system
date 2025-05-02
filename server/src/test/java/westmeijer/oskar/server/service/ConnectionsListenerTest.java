package westmeijer.oskar.server.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;

import java.net.ServerSocket;
import java.net.Socket;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import westmeijer.oskar.server.ServerMain;
import westmeijer.oskar.server.client.ClientListener;
import westmeijer.oskar.server.service.model.ClientDetails;

class ConnectionsListenerTest {

  @Test
  @SneakyThrows
  void shouldNotStartAcceptingConnections() {
    // Given
    try (var clientServiceMock = mockStatic(ClientService.class);
        var historizedEventServiceMock = mockStatic(HistorizedEventService.class);
        var mainMock = mockStatic(ServerMain.class)) {

      var serverSocket = mock(ServerSocket.class);
      var clientService = mock(ClientService.class);
      var historizedEventService = mock(HistorizedEventService.class);

      clientServiceMock.when(ClientService::getInstance).thenReturn(clientService);
      historizedEventServiceMock.when(HistorizedEventService::getInstance).thenReturn(historizedEventService);
      mainMock.when(ServerMain::isListening).thenReturn(false);

      var listener = new ConnectionsListener(serverSocket);

      // When
      listener.listenForConnection();

      // Then
      then(clientService).should(never()).registerClient(any());
      then(serverSocket).should(never()).accept();
    }
  }

  @Test
  @SneakyThrows
  void shouldHandleNewConnectionAndStartThread() {
    // Given
    try (var clientServiceMock = mockStatic(ClientService.class);
        var historizedEventServiceMock = mockStatic(HistorizedEventService.class);
        var mainMock = mockStatic(ServerMain.class)) {

      var clientSocket = mock(Socket.class);
      var clientListener = mock(ClientListener.class);
      var clientDetails = mock(ClientDetails.class);
      var clientService = mock(ClientService.class);
      var historizedEventService = mock(HistorizedEventService.class);
      var serverSocket = mock(ServerSocket.class);

      clientServiceMock.when(ClientService::getInstance).thenReturn(clientService);
      historizedEventServiceMock.when(HistorizedEventService::getInstance).thenReturn(historizedEventService);
      mainMock.when(ServerMain::isListening).thenReturn(true).thenReturn(false);

      given(serverSocket.accept()).willReturn(clientSocket);
      given(clientService.registerClient(clientSocket)).willReturn(clientListener);
      given(clientListener.getClientDetails()).willReturn(clientDetails);
      given(clientService.getClients()).willReturn(new java.util.ArrayList<>());
      given(clientService.getClientsCount()).willReturn(1);

      var listener = new ConnectionsListener(serverSocket);

      // When
      listener.listenForConnection();

      // Then
      then(clientService).should().registerClient(clientSocket);
    }
  }

}