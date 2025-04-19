package westmeijer.oskar.server.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import westmeijer.oskar.shared.model.ClientDetails;

public class ClientConnectionRepository {

  private final List<ClientDetails> clientConnections = new ArrayList<>();

  public void insertConntection(ClientDetails clientDetails) {
    Objects.requireNonNull(clientDetails, "clientConnection is required");
    clientConnections.add(clientDetails);
  }

  public void updateDissconect(ClientDetails clientDetails) {
    throw new RuntimeException("Disconnecting not yet implemented");
  }

  public List<ClientDetails> getClients() {
    return List.copyOf(clientConnections);
  }

}
