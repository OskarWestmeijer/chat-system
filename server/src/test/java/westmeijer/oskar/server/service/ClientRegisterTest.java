package westmeijer.oskar.server.service;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;

import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import westmeijer.oskar.server.client.ClientListener;
import westmeijer.oskar.server.service.model.ClientDetails;

class ClientRegisterTest {

  private ClientRegister service;
  private ClientListener clientListener;

  @BeforeEach
  void resetService() {
    clientListener = mock(ClientListener.class);

    service = ClientRegister.getInstance();
    service.getClients().forEach(service::unregisterClient); // remove all clients
  }

  @Test
  void shouldBuildSingletonInstance() {
    var instance1 = ClientRegister.getInstance();
    var instance2 = ClientRegister.getInstance();

    then(instance1).isSameAs(instance2);
  }

  @Test
  @SneakyThrows
  void shouldRegisterClient() {
    // when
    var result = service.registerClient(clientListener);

    // then
    then(result).isEqualTo(clientListener);
    then(service.getClients())
        .hasSize(1)
        .contains(clientListener);
  }

  @Test
  @SneakyThrows
  void shouldUnregisterClient() {
    // given
    var client = service.registerClient(clientListener);

    // when
    var removed = service.unregisterClient(client);

    // then
    then(removed).isTrue();
    then(service.getClients())
        .isEmpty();
  }

  @Test
  @SneakyThrows
  void shouldReturnFilteredClients() {
    // given
    var filterOut = mock(ClientListener.class);
    var filterOutDetails = mock(ClientDetails.class);
    given(filterOut.getClientDetails()).willReturn(filterOutDetails);

    given(clientListener.getClientDetails()).willReturn(mock(ClientDetails.class));
    service.registerClient(clientListener);
    service.registerClient(filterOut);

    // when
    List<ClientListener> filteredClients = service.getClients(List.of(filterOutDetails));

    // then
    then(filteredClients).containsExactly(clientListener);
    then(service.getClients()).hasSize(2);
    then(service.getClientsCount()).isEqualTo(2);
  }

  @Test
  @SneakyThrows
  void shouldReturnAllClientsWhenNoFilterProvided() {
    // given
    var client2 = mock(ClientListener.class);
    service.registerClient(clientListener);
    service.registerClient(client2);

    // when
    List<ClientListener> filteredClients = service.getClients();

    // then
    then(filteredClients).containsExactlyInAnyOrder(clientListener, client2);
    then(service.getClients()).hasSize(2);
    then(service.getClientsCount()).isEqualTo(2);
  }

  @Test
  @SneakyThrows
  void shouldReturnClientCount() {
    // given
    service.registerClient(clientListener);
    service.registerClient(mock(ClientListener.class));

    // when
    var count = service.getClientsCount();

    // then
    then(count).isEqualTo(2);
  }

  @Test
  void shouldReturnEmptyClient() {
    var client = service.getClient(mock(ClientDetails.class));
    then(client).isEmpty();
  }

  @Test
  void shouldReturnEmptyClientOnNoMatch() {
    var clientDetails = mock(ClientDetails.class);
    var listener = mock(ClientListener.class);
    given(listener.getClientDetails()).willReturn(clientDetails);

    service.registerClient(listener);

    var client = service.getClient(mock(ClientDetails.class));
    then(client).isEmpty();
  }

  @Test
  void shouldReturnClient() {
    var clientDetails = mock(ClientDetails.class);
    var listener = mock(ClientListener.class);
    given(listener.getClientDetails()).willReturn(clientDetails);

    service.registerClient(listener);

    var client = service.getClient(clientDetails);
    then(client).contains(listener);
  }


}
