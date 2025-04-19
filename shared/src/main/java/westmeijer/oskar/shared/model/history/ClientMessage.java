package westmeijer.oskar.shared.model.history;

import java.io.Serial;
import java.io.Serializable;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import westmeijer.oskar.shared.model.ClientDetails;
import westmeijer.oskar.shared.model.ClientLogger;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class ClientMessage extends HistoryEvent implements Serializable, ClientLogger {

  @Serial
  private static final long serialVersionUID = 15071992L;

  String message;

  ClientDetails client;

  public ClientMessage(String message, ClientDetails client) {
    super(HistoryEventType.CHAT_MESSAGE_RECEIVED);
    this.message = message;
    this.client = client;
  }

  @Override
  public String getClientLog() {
    return String.format("%s: %s", client.getId(), message);
  }

}
