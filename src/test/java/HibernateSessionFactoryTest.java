import de.schlaumeijer.dal.ChatMessageEntity;
import de.schlaumeijer.dal.ClientConnectionEntity;
import de.schlaumeijer.dal.HibernateSessionFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.UUID;

public class HibernateSessionFactoryTest {

    @Test
    public void insertChatMessageEntity() {
        ClientConnectionEntity clientConnectionEntity = new ClientConnectionEntity();
        ChatMessageEntity chatMessageEntity = createChatMessageEntity();
        Session session = HibernateSessionFactory.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.save(chatMessageEntity);
        transaction.commit();
        session.close();
    }

    @Test
    public void insertClientConnectionEntity() {
        ClientConnectionEntity clientConnectionEntity = createClientConnectionEntity();
        Session session = HibernateSessionFactory.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.save(clientConnectionEntity);
        transaction.commit();
        session.close();
    }

    @Test
    public void readMessageByUUID() {
        Session session = HibernateSessionFactory.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        ChatMessageEntity chatMessageEntity = session.get(ChatMessageEntity.class, UUID.fromString("d6d0da84-9c7a-4e61-b14e-eabaf3e46bed"));
        session.close();
        Assert.assertEquals(chatMessageEntity.getSenderName(), "Blug");
        Assert.assertEquals(chatMessageEntity.getMessage(), "HelloFromMe");
    }

    private ChatMessageEntity createChatMessageEntity() {
        ChatMessageEntity chatMessageEntity = new ChatMessageEntity();
        chatMessageEntity.setUuid(UUID.randomUUID());
        chatMessageEntity.setDate(new Date(System.currentTimeMillis()));
        chatMessageEntity.setMessage("HelloFromMe");
        chatMessageEntity.setSenderName("Blug");
        chatMessageEntity.setSenderIp("localhost");
        return chatMessageEntity;
    }

    private ClientConnectionEntity createClientConnectionEntity() {
        ClientConnectionEntity clientConnectionEntity = new ClientConnectionEntity();
        clientConnectionEntity.setUuid(UUID.randomUUID());
        clientConnectionEntity.setName("oskar");
        clientConnectionEntity.setIpAdress("localhost");
        clientConnectionEntity.setConnectionDate(new Date(System.currentTimeMillis()));
        clientConnectionEntity.setDisconnectionDate(new Date(System.currentTimeMillis() + 500000));
        return clientConnectionEntity;
    }

}
