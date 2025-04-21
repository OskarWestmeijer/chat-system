package westmeijer.oskar.server.service.model;

import java.time.temporal.ChronoUnit;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import westmeijer.oskar.shared.model.response.ClientLogger;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class ClientMessage extends HistorizedEvent implements ClientLogger {

  String message;

  ClientDetails client;

  public ClientMessage(String message, ClientDetails client) {
    super(HistorizedEventType.CHAT_MESSAGE_RECEIVED);
    this.message = message;
    this.client = client;
  }

  @Override
  public String getClientLog() {
    return String.format("%s: %s", client.getTag(), message);
  }

  @Override
  public String getHistorizedLog() {
    return String.format("%s %s: %s", super.getRecordedAt().truncatedTo(ChronoUnit.SECONDS), client.getTag(), message);
  }

}
