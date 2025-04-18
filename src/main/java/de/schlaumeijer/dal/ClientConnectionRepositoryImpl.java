package de.schlaumeijer.dal;

import de.schlaumeijer.bl.ClientConnectionRepository;
import de.schlaumeijer.shared.ClientConnectionDto;
import java.util.ArrayList;
import java.util.List;

public class ClientConnectionRepositoryImpl implements ClientConnectionRepository {

  private final ClientConnectionMapper clientConnectionMapper = new ClientConnectionMapper();
  private final List<ClientConnectionEntity> clientConnectionEntities = new ArrayList<>();

  @Override
  public void insertConntection(ClientConnectionDto clientConnectionDto) {
    ClientConnectionEntity clientConnectionEntity = clientConnectionMapper.mapToEntity(clientConnectionDto);
    clientConnectionEntities.add(clientConnectionEntity);
  }

  @Override
  public void updateDissconect(ClientConnectionDto clientConnectionDto) {
    ClientConnectionEntity clientConnectionEntity = clientConnectionMapper.mapToEntity(clientConnectionDto);
    clientConnectionEntities.add(clientConnectionEntity);
  }

  @Override
  public List<ClientConnectionDto> readHistoryOfConnections() {
    return clientConnectionMapper.mapToBoList(clientConnectionEntities);
  }
}
