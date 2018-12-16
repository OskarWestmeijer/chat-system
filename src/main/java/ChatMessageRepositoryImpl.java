import org.hibernate.Session;

import java.util.Date;
import java.util.List;
import java.util.UUID;


public class ChatMessageRepositoryImpl implements ChatMessageRepository {

    private ChatMessageMapper chatMessageMapper;

    @Override
    public void insertMessage(ChatMessageDto chatMessageDto) {

        ChatMessageEntity chatMessageEntity = chatMessageMapper.mapToEntity(chatMessageDto);

        Session session = HibernateSessionFactory.sessionFactory.openSession();
        session.beginTransaction();

        chatMessageEntity = new ChatMessageEntity();
        chatMessageEntity.setUuid(UUID.randomUUID());
        chatMessageEntity.setDate(new Date(System.currentTimeMillis()));
        chatMessageEntity.setMessage("HelloFromMe");
        chatMessageEntity.setSenderName("Blug");
        chatMessageEntity.setSenderIp("localhost");
        session.save(chatMessageEntity);


        session.getTransaction().commit();
        HibernateSessionFactory.sessionFactory.close();
    }

    @Override
    public List<ChatMessageDto> readAllMessagesByIp(String ip) {
        return null;
    }

    @Override
    public List<ChatMessageDto> readAllMessagesByName(String name) {
        return null;
    }

    @Override
    public List<ChatMessageDto> readAllMessages() {
        Session session = HibernateSessionFactory.sessionFactory.openSession();
        session.beginTransaction();

        ChatMessageEntity chatMessageEntity = new ChatMessageEntity();

        session.save(chatMessageEntity);



        session.getTransaction().commit();
        HibernateSessionFactory.sessionFactory.close();
        return null;
    }
}
