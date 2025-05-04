package westmeijer.oskar.server.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import westmeijer.oskar.server.client.ClientListener;
import westmeijer.oskar.server.service.model.ClientDetails;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientRegister {

  private final List<ClientListener> clients = new ArrayList<>();

  @Getter
  private static final ClientRegister instance = new ClientRegister();

  public ClientListener registerClient(ClientListener clientListener) {
    clients.add(clientListener);
    log.info("Registered client: {}", clientListener.getClientDetails());
    return clientListener;
  }

  public boolean unregisterClient(ClientListener client) {
    log.info("Unregistering client: {}", client.getClientDetails());
    return clients.remove(client);
  }

  public List<ClientListener> getClients() {
    return getClients(Collections.emptyList());
  }

  /**
   *
   * @param filter not part of returned list
   * @return clients without filter content
   */
  public List<ClientListener> getClients(List<ClientDetails> filter) {
    return clients.stream()
        .filter(client -> !filter.contains(client.getClientDetails()))
        .toList();
  }

  public Optional<ClientListener> getClient(ClientDetails clientDetails) {
    return clients
        .stream()
        .filter(client -> client.getClientDetails().equals(clientDetails))
        .findFirst();
  }

  public Integer getClientsCount() {
    return clients.size();
  }

}
