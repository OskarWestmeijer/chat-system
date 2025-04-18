package de.schlaumeijer.dal;

import java.util.Date;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageEntity {

  private UUID uuid;

  private String senderIp;

  private String senderName;

  private String message;

  private Date date;

}


