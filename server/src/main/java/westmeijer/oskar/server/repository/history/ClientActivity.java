package westmeijer.oskar.server.repository.history;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import lombok.EqualsAndHashCode;
import lombok.Value;
import westmeijer.oskar.shared.model.response.ClientLogger;

@Value
@EqualsAndHashCode(callSuper = true)
public class ClientActivity extends HistoryEvent implements Serializable, ClientLogger {

  @Serial
  private static final long serialVersionUID = 665L;

  ClientDetails clientDetails;

  public ClientActivity(HistoryEventType event, ClientDetails clientDetails) {
    super(event);
    this.clientDetails = Objects.requireNonNull(clientDetails, "clientDetails is required");
  }

  @Override
  public String getClientLog() {
    return String.format("Client: %s, event: %s", clientDetails.getId(), super.getEvent());
  }
}
