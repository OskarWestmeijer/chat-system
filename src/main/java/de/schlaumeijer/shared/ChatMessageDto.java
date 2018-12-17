package de.schlaumeijer.shared;

import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ChatMessageDto implements Serializable {

    private static final long serialVersionUID = 15071992;

    private UUID uuid;

    private String senderIp;

    private String senderName;

    private String message;

    private Date date;


}
