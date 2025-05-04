package westmeijer.oskar.server.service;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;

import java.net.ServerSocket;
import java.net.Socket;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import westmeijer.oskar.server.ServerMain;

class ConnectionListenerTest {

  @BeforeEach
  void setup() {
    ConnectionListener.reset();
  }

  @Test
  @SneakyThrows
  void shouldNotStartAcceptingConnectionsWhenIsListeningFalse() {
    // given
    try (var mainMock = mockStatic(ServerMain.class)) {

      var serverSocket = mock(ServerSocket.class);
      var connectionProcessor = mock(ConnectionProcessor.class);

      mainMock.when(ServerMain::isListening).thenReturn(false);

      var listener = ConnectionListener.init(serverSocket, connectionProcessor);

      // when
      listener.listenForConnection();

      // then
      then(serverSocket).should(never()).accept();
      then(connectionProcessor).shouldHaveNoInteractions();
    }
  }

  @Test
  @SneakyThrows
  void shouldHandleNewConnectionAndStartThread() {
    // given
    try (var mainMock = mockStatic(ServerMain.class)) {

      var serverSocket = mock(ServerSocket.class);
      var connectionProcessor = mock(ConnectionProcessor.class);

      var listener = ConnectionListener.init(serverSocket, connectionProcessor);
      mainMock.when(ServerMain::isListening).thenReturn(true).thenReturn(false);

      var clientSocket = mock(Socket.class);
      given(serverSocket.accept()).willReturn(clientSocket);

      // When
      listener.listenForConnection();

      // Then
      then(serverSocket).should().accept();
      then(connectionProcessor).should().process(clientSocket);
    }
  }

}