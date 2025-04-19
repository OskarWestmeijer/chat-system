package de.schlaumeijer.service.model;

import java.io.Serial;
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

  @Serial
  private static final long serialVersionUID = 15071992L;

  private UUID uuid;

  private String senderIp;

  private String senderName;

  private String message;

  private Date date;

  private ClientConnectionDto clientConnectionDto;

}
