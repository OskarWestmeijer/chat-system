package de.schlaumeijer.bl;

import de.schlaumeijer.shared.ClientConnectionDto;

import java.util.List;

public interface ClientConnectionRepository {

    void insertConntection(ClientConnectionDto clientConnectionDto);

    void updateDissconect(ClientConnectionDto clientConnectionDto);

    List<ClientConnectionDto> readHistoryOfConnections();

}
