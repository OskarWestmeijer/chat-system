package westmeijer.oskar.server.service;


import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import westmeijer.oskar.server.client.ClientListener;
import westmeijer.oskar.server.client.ClientStreamProvider;
import westmeijer.oskar.server.service.model.ClientDetails;

class ClientInitializerTest {

  @Test
  @SneakyThrows
  void shouldInitializeClientListener() {
    var clientSocket = mock(Socket.class);
    var inetAddress = mock(InetAddress.class);
    var inputStream = mock(ObjectInputStream.class);
    var outputStream = mock(ObjectOutputStream.class);

    given(clientSocket.getInetAddress()).willReturn(inetAddress);
    given(inetAddress.getHostAddress()).willReturn("127.0.0.1");

    try (
        MockedStatic<ClientDetails> detailsMock = mockStatic(ClientDetails.class);
        MockedStatic<ClientStreamProvider> streamMock = mockStatic(ClientStreamProvider.class)
    ) {
      var details = mock(ClientDetails.class);
      detailsMock.when(() -> ClientDetails.from("127.0.0.1")).thenReturn(details);
      streamMock.when(() -> ClientStreamProvider.createInput(clientSocket)).thenReturn(inputStream);
      streamMock.when(() -> ClientStreamProvider.createOutput(clientSocket)).thenReturn(outputStream);

      var history = mock(HistorizedEventService.class);
      var register = mock(ClientRegister.class);

      var result = ClientInitializer.getInstance().init(clientSocket, history, register);

      then(result).isNotNull().isInstanceOf(ClientListener.class);

      detailsMock.verify(() -> ClientDetails.from("127.0.0.1"));
      streamMock.verify(() -> ClientStreamProvider.createInput(clientSocket));
      streamMock.verify(() -> ClientStreamProvider.createOutput(clientSocket));
    }
  }

}