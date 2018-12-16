import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.UUID;

public class HibernateSessionFactoryTest {

    @Test
    public void insertChatMessageEntity() {

        Session session = HibernateSessionFactory.sessionFactory.openSession();
        session.beginTransaction();

        ChatMessageEntity chatMessageEntity = new ChatMessageEntity();
        chatMessageEntity.setUuid(UUID.randomUUID());
        chatMessageEntity.setDate(new Date(System.currentTimeMillis()));
        chatMessageEntity.setMessage("HelloFromMe");
        chatMessageEntity.setSenderName("Blug");
        chatMessageEntity.setSenderIp("localhost");
        session.save(chatMessageEntity);

        session.getTransaction().commit();
    }

    @Test
    public void readMessage(){
        Session session = HibernateSessionFactory.sessionFactory.openSession();
        session.beginTransaction();
        ChatMessageEntity chatMessageEntity =  (ChatMessageEntity) session.get(ChatMessageEntity.class, UUID.fromString("fca45063-c908-4525-a930-4ca3dbd61389"));
        Assert.assertEquals(chatMessageEntity.getSenderName(),"oskar");
        Assert.assertEquals(chatMessageEntity.getMessage(),"HelloWorld from MariaDB!");
    }

}
