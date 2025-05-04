package westmeijer.oskar.server.service;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyInt;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.mockStatic;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

import java.net.ServerSocket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class ServerInitializerTest {

  MockedStatic<ClientRegister> clientRegisterMock;
  MockedStatic<HistorizedEventService> historizedEventServiceMock;
  MockedStatic<ClientInitializer> clientInitializerMock;
  MockedStatic<ConnectionProcessor> connectionProcessorMock;
  MockedStatic<ConnectionListener> connectionListenerMock;

  @BeforeEach
  void setUp() {
    clientRegisterMock = mockStatic(ClientRegister.class);
    historizedEventServiceMock = mockStatic(HistorizedEventService.class);
    clientInitializerMock = mockStatic(ClientInitializer.class);
    connectionProcessorMock = mockStatic(ConnectionProcessor.class);
    connectionListenerMock = mockStatic(ConnectionListener.class);
  }

  @Test
  void shouldInitializeAndListen() {
    var clientRegister = mock(ClientRegister.class);
    var historizedEventService = mock(HistorizedEventService.class);
    var clientInitializer = mock(ClientInitializer.class);
    var connectionProcessor = mock(ConnectionProcessor.class);
    var server = mock(ServerSocket.class);
    var serverController = mock(ConnectionListener.class);

    clientRegisterMock.when(ClientRegister::getInstance).thenReturn(clientRegister);
    historizedEventServiceMock.when(HistorizedEventService::getInstance).thenReturn(historizedEventService);
    clientInitializerMock.when(ClientInitializer::getInstance).thenReturn(clientInitializer);
    connectionProcessorMock.when(() -> ConnectionProcessor.init(any(), any(), any())).thenReturn(connectionProcessor);
    connectionListenerMock.when(() -> ConnectionListener.serverSocket(anyInt())).thenReturn(server);
    connectionListenerMock.when(() -> ConnectionListener.init(server, connectionProcessor)).thenReturn(serverController);

    willDoNothing().given(serverController).listenForConnection();

    // when
    ServerInitializer.getInstance().init(1234);

    // then
    then(serverController).should().listenForConnection();
  }
}
