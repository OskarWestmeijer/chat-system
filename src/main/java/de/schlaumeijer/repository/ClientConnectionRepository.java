package de.schlaumeijer.repository;

import de.schlaumeijer.service.model.ClientConnectionDto;
import java.util.List;

public interface ClientConnectionRepository {

  void insertConntection(ClientConnectionDto clientConnectionDto);

  void updateDissconect(ClientConnectionDto clientConnectionDto);

  List<ClientConnectionDto> getClients();

}
