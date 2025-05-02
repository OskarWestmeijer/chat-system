package westmeijer.oskar.client.service;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import westmeijer.oskar.client.service.server.ServerListener;
import westmeijer.oskar.client.service.server.ServerProcessor;
import westmeijer.oskar.client.service.terminal.TerminalListener;
import westmeijer.oskar.client.service.terminal.TerminalProcessor;

class ClientInitializerTest {

  private final ClientInitializer clientInitializer = new ClientInitializer();

  @Test
  void initTest() {
    // Mocks for dependencies
    var mockSocket = mock(Socket.class);
    var mockInputStream = mock(ObjectInputStream.class);
    var mockOutputStream = mock(ObjectOutputStream.class);
    var mockStreamProvider = mock(StreamProvider.class);
    var mockServerProcessor = mock(ServerProcessor.class);
    var mockServerListener = mock(ServerListener.class);
    var mockTerminalProcessor = mock(TerminalProcessor.class);
    var mockTerminalListener = mock(TerminalListener.class);
    var mockExecutorService = mock(ExecutorService.class);
    var mockClientService = mock(ClientService.class);

    try (
        MockedStatic<InitializerFactory> factory = mockStatic(InitializerFactory.class);
        MockedStatic<ServerProcessor> serverProcessorStatic = mockStatic(ServerProcessor.class);
        MockedStatic<ServerListener> serverListenerStatic = mockStatic(ServerListener.class);
        MockedStatic<TerminalProcessor> terminalProcessorStatic = mockStatic(TerminalProcessor.class);
        MockedStatic<TerminalListener> terminalListenerStatic = mockStatic(TerminalListener.class);
        MockedStatic<Executors> executorStatic = mockStatic(Executors.class);
        MockedStatic<ClientService> clientServiceStatic = mockStatic(ClientService.class)
    ) {
      // Mock static factory methods
      factory.when(() -> InitializerFactory.createSocket(anyString(), anyInt())).thenReturn(mockSocket);
      factory.when(() -> InitializerFactory.createInput(mockSocket)).thenReturn(mockInputStream);
      factory.when(() -> InitializerFactory.createOutput(mockSocket)).thenReturn(mockOutputStream);
      factory.when(() -> InitializerFactory.createStreamProvider(mockSocket, mockInputStream, mockOutputStream))
          .thenReturn(mockStreamProvider);

      // Mock singleton initializers
      serverProcessorStatic.when(ServerProcessor::getInstance).thenReturn(mockServerProcessor);
      serverListenerStatic.when(() -> ServerListener.init(mockStreamProvider, mockServerProcessor))
          .thenReturn(mockServerListener);
      terminalProcessorStatic.when(() -> TerminalProcessor.init(mockStreamProvider))
          .thenReturn(mockTerminalProcessor);
      terminalListenerStatic.when(() -> TerminalListener.init(mockStreamProvider, mockTerminalProcessor))
          .thenReturn(mockTerminalListener);
      executorStatic.when(() -> Executors.newFixedThreadPool(2)).thenReturn(mockExecutorService);
      clientServiceStatic.when(() -> ClientService.init(mockStreamProvider, mockExecutorService, mockTerminalListener, mockServerListener))
          .thenReturn(mockClientService);

      var actual = clientInitializer.init();

      BDDAssertions.then(actual).isEqualTo(mockClientService);
    }
  }
}
