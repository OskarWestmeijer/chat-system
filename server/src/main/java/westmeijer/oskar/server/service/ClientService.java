package westmeijer.oskar.server.service;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import westmeijer.oskar.server.client.ClientFactory;
import westmeijer.oskar.server.client.ClientListener;
import westmeijer.oskar.server.service.model.ClientDetails;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientService {

  private final List<ClientListener> clients = new ArrayList<>();

  private static ClientService instance;

  public static synchronized ClientService getInstance() {
    if (ClientService.instance == null) {
      ClientService.instance = new ClientService();
    }
    return ClientService.instance;
  }

  public ClientListener registerClient(Socket socket) {
    var clientDetails = ClientDetails.from(socket.getInetAddress().getHostAddress(), generateUniqueTag());
    var clientListener = ClientFactory.create(socket, clientDetails);
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

  public List<ClientListener> getClients(List<ClientListener> filter) {
    return clients.stream()
        .filter(client -> !filter.contains(client))
        .toList();
  }

  public Integer getClientsCount() {
    return clients.size();
  }

  private String generateUniqueTag() {
    return "#" + UUID.randomUUID().toString().substring(0, 3);
  }

}
