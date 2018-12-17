package de.schlaumeijer.dal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "chat_messages")
public class ChatMessageEntity {

    @Id
    @Type(type="uuid-char")
    @Column(name = "uuid")
    private UUID uuid;

    @Column(name = "sender_ip")
    private String senderIp;

    @Column(name = "sender_name")
    private String senderName;

    @Column(name = "message")
    private String message;

    @Column(name = "date")
    private Date date;

}


