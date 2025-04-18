package de.schlaumeijer.repository;

import de.schlaumeijer.service.model.ClientConnectionDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClientConnectionRepositoryImpl implements ClientConnectionRepository {

  private final List<ClientConnectionDto> clientConnections = new ArrayList<>();

  @Override
  public void insertConntection(ClientConnectionDto clientConnectionDto) {
    Objects.requireNonNull(clientConnectionDto, "clientConnection is required");
    clientConnections.add(clientConnectionDto);
  }

  @Override
  public void updateDissconect(ClientConnectionDto clientConnectionDto) {
    throw new RuntimeException("Disconnecting not yet implemented");
  }

  @Override
  public List<ClientConnectionDto> getClients() {
    return List.copyOf(clientConnections);
  }

}
