package de.schlaumeijer.shared;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDto implements Serializable {

  private UUID uuid;

  private String senderIp;

  private String senderName;

  private String message;

  private Date date;


}
