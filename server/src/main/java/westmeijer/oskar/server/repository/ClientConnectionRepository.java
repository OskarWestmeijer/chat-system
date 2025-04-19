package westmeijer.oskar.server.repository;

import java.util.List;
import westmeijer.oskar.shared.model.ClientConnectionDto;

public interface ClientConnectionRepository {

  void insertConntection(ClientConnectionDto clientConnectionDto);

  void updateDissconect(ClientConnectionDto clientConnectionDto);

  List<ClientConnectionDto> getClients();

}
