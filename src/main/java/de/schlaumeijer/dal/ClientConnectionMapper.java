package de.schlaumeijer.dal;

import de.schlaumeijer.shared.ClientConnectionDto;
import java.util.ArrayList;
import java.util.List;

public class ClientConnectionMapper {

  public List<ClientConnectionDto> mapToBoList(List<ClientConnectionEntity> clientConnectionEntitieList) {
    List<ClientConnectionDto> clientConnectionDtoList = new ArrayList<>();
    for (ClientConnectionEntity clientConnectionEntity : clientConnectionEntitieList) {
      ClientConnectionDto clientConnectionDto = mapToBo(clientConnectionEntity);
      clientConnectionDtoList.add(clientConnectionDto);
    }
    return clientConnectionDtoList;
  }

  public ClientConnectionEntity mapToEntity(ClientConnectionDto clientConnectionDto) {
    return new ClientConnectionEntity(clientConnectionDto.getUuid(),
        clientConnectionDto.getIpAdress(), clientConnectionDto.getName(),
        clientConnectionDto.getConnectionDate(), clientConnectionDto.getDisconnectionDate());
  }

  public ClientConnectionDto mapToBo(ClientConnectionEntity clientConnectionEntity) {
    return new ClientConnectionDto(clientConnectionEntity.getUuid(),
        clientConnectionEntity.getIpAdress(), clientConnectionEntity.getName(),
        clientConnectionEntity.getConnectionDate(), clientConnectionEntity.getDisconnectionDate());
  }

}
