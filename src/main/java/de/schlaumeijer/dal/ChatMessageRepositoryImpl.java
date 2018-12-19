package de.schlaumeijer.dal;

import de.schlaumeijer.shared.ChatMessageDto;
import de.schlaumeijer.bl.ChatMessageRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;


public class ChatMessageRepositoryImpl implements ChatMessageRepository {

    private ChatMessageMapper chatMessageMapper = new ChatMessageMapper();

    @Override
    public void insertMessage(ChatMessageDto chatMessageDto) {
        ChatMessageEntity chatMessageEntity = chatMessageMapper.mapToEntity(chatMessageDto);

        Session session = HibernateSessionFactory.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.save(chatMessageEntity);
        transaction.commit();
        session.close();
    }

    @Override
    public List<ChatMessageDto> readAllMessagesByIp(String ip) {return null;}

    @Override
    public List<ChatMessageDto> readAllMessagesByName(String name) {
        return null;
    }

    @Override
    public List<ChatMessageDto> readAllMessages() {
        Session session = HibernateSessionFactory.getSessionFactory().openSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<ChatMessageEntity> criteria = builder.createQuery(ChatMessageEntity.class);
        criteria.from(ChatMessageEntity.class);

        List<ChatMessageEntity> chatMessageEntityList = session.createQuery(criteria).getResultList();
        session.close();
        return chatMessageMapper.mapToDtoList(chatMessageEntityList);
    }
}
