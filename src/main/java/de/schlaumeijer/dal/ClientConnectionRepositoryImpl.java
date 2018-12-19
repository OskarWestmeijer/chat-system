package de.schlaumeijer.dal;

import de.schlaumeijer.shared.ClientConnectionDto;
import de.schlaumeijer.bl.ClientConnectionRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

public class ClientConnectionRepositoryImpl implements ClientConnectionRepository {

    private ClientConnectionMapper clientConnectionMapper = new ClientConnectionMapper();

    @Override
    public void insertConntection(ClientConnectionDto clientConnectionDto) {
        ClientConnectionEntity clientConnectionEntity = clientConnectionMapper.mapToEntity(clientConnectionDto);

        Session session = HibernateSessionFactory.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.save(clientConnectionEntity);
        transaction.commit();
        session.close();
    }

    @Override
    public void updateDissconect(ClientConnectionDto clientConnectionDto) {
        ClientConnectionEntity clientConnectionEntity = clientConnectionMapper.mapToEntity(clientConnectionDto);

        Session session = HibernateSessionFactory.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.save(clientConnectionEntity);
        transaction.commit();
        session.close();
    }

    @Override
    public List<ClientConnectionDto> readHistoryOfConnections() {
        Session session = HibernateSessionFactory.getSessionFactory().openSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<ClientConnectionEntity> criteria = builder.createQuery(ClientConnectionEntity.class);
        criteria.from(ClientConnectionEntity.class);

        List<ClientConnectionEntity> clientConnectionEntityList = session.createQuery(criteria).getResultList();
        session.close();
        return clientConnectionMapper.mapToBoList(clientConnectionEntityList);
    }
}
