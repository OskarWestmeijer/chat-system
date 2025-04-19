package westmeijer.oskar.shared.model;

import java.io.Serial;
import java.io.Serializable;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class ChatMessageDto extends PublicEvent implements Serializable {

  @Serial
  private static final long serialVersionUID = 15071992L;

  String message;

  ClientConnectionDto client;

  public ChatMessageDto(String message, ClientConnectionDto client) {
    super(ServerEvent.CHAT_MESSAGE_RECEIVED);
    this.message = message;
    this.client = client;
  }

}
