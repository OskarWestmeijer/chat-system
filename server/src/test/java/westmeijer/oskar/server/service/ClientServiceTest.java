package westmeijer.oskar.server.service;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.Mockito.mockStatic;

import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import westmeijer.oskar.server.client.ClientFactory;
import westmeijer.oskar.server.client.ClientListener;
import westmeijer.oskar.server.service.model.ClientDetails;

class ClientServiceTest {

  private ClientService service;
  private Socket socket;
  private ClientListener clientListener;

  @BeforeEach
  void resetService() {
    socket = mock(Socket.class);
    clientListener = mock(ClientListener.class);

    service = ClientService.getInstance();
    service.getClients().forEach(service::unregisterClient); // remove all clients
  }

  @Test
  void shouldBuildSingletonInstance() {
    var instance1 = ClientService.getInstance();
    var instance2 = ClientService.getInstance();

    then(instance1).isSameAs(instance2);
  }

  @Test
  @SneakyThrows
  void shouldRegisterClient() {
    // given
    var inet = mock(InetAddress.class);
    given(socket.getInetAddress()).willReturn(inet);
    given(inet.getHostAddress()).willReturn("123.456.789");

    var details = ClientDetails.from("123.456.789", "#xyz");
    given(clientListener.getClientDetails()).willReturn(details);

    try (MockedStatic<ClientFactory> mockedFactory = mockStatic(ClientFactory.class)) {
      mockedFactory.when(() -> ClientFactory.create(eq(socket), any(ClientDetails.class)))
          .thenReturn(clientListener);

      // when
      var result = service.registerClient(socket);

      // then
      then(result).isEqualTo(clientListener);
      then(service.getClients())
          .hasSize(1)
          .contains(clientListener);
    }
  }

  @Test
  @SneakyThrows
  void shouldUnregisterClient() {
    // given
    var socket = mock(Socket.class);
    given(socket.getInetAddress()).willReturn(mock(InetAddress.class));
    given(socket.getInetAddress().getHostAddress()).willReturn("192.168.0.1");

    try (MockedStatic<ClientFactory> mockedFactory = mockStatic(ClientFactory.class)) {
      mockedFactory.when(() -> ClientFactory.create(eq(socket), any(ClientDetails.class)))
          .thenReturn(clientListener);

      var client = service.registerClient(socket);

      // when
      var removed = service.unregisterClient(client);

      // then
      then(removed).isTrue();
      then(service.getClients())
          .isEmpty();
    }
  }

  @Test
  @SneakyThrows
  void shouldReturnFilteredClients() {
    // given
    var socket1 = mock(Socket.class);
    var socket2 = mock(Socket.class);
    var inet1 = mock(InetAddress.class);
    var inet2 = mock(InetAddress.class);

    given(socket1.getInetAddress()).willReturn(inet1);
    given(socket2.getInetAddress()).willReturn(inet2);
    given(inet1.getHostAddress()).willReturn("10.0.0.1");
    given(inet2.getHostAddress()).willReturn("10.0.0.2");

    try (MockedStatic<ClientFactory> mockedFactory = mockStatic(ClientFactory.class)) {
      mockedFactory.when(() -> ClientFactory.create(any(Socket.class), any(ClientDetails.class)))
          .thenReturn(clientListener, mock(ClientListener.class));

      var client1 = service.registerClient(socket1);
      var client2 = service.registerClient(socket2);

      // when
      List<ClientListener> filteredClients = service.getClients(List.of(client1));

      // then
      then(filteredClients).containsExactly(client2);
      then(service.getClients()).hasSize(2);
    }
  }

  @Test
  @SneakyThrows
  void shouldReturnAllClientsWhenNoFilterProvided() {
    // given
    var socket = mock(Socket.class);
    var inet = mock(InetAddress.class);
    given(socket.getInetAddress()).willReturn(inet);
    given(inet.getHostAddress()).willReturn("localhost");

    try (MockedStatic<ClientFactory> mockedFactory = mockStatic(ClientFactory.class)) {
      mockedFactory.when(() -> ClientFactory.create(eq(socket), any(ClientDetails.class)))
          .thenReturn(clientListener);
      var client = service.registerClient(socket);

      // when
      var clients = service.getClients();

      // then
      then(clients).containsExactly(client);
    }
  }

  @Test
  @SneakyThrows
  void shouldReturnClientCount() {
    // given
    var socket = mock(Socket.class);
    var inet = mock(InetAddress.class);
    given(socket.getInetAddress()).willReturn(inet);
    given(inet.getHostAddress()).willReturn("client.host");

    try (MockedStatic<ClientFactory> mockedFactory = mockStatic(ClientFactory.class)) {
      mockedFactory.when(() -> ClientFactory.create(eq(socket), any(ClientDetails.class)))
          .thenReturn(clientListener);
      // TODO: it does not catch duplicates

      service.registerClient(socket);
      service.registerClient(socket);

      // when
      var count = service.getClientsCount();

      // then
      then(count).isEqualTo(2);
    }
  }
}
