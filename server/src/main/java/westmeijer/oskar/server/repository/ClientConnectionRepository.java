package westmeijer.oskar.server.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import westmeijer.oskar.shared.model.ClientConnectionDto;

public class ClientConnectionRepository {

  private final List<ClientConnectionDto> clientConnections = new ArrayList<>();

  public void insertConntection(ClientConnectionDto clientConnectionDto) {
    Objects.requireNonNull(clientConnectionDto, "clientConnection is required");
    clientConnections.add(clientConnectionDto);
  }

  public void updateDissconect(ClientConnectionDto clientConnectionDto) {
    throw new RuntimeException("Disconnecting not yet implemented");
  }

  public List<ClientConnectionDto> getClients() {
    return List.copyOf(clientConnections);
  }

}
