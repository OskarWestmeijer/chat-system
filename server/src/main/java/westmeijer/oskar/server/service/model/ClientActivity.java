package westmeijer.oskar.server.service.model;

import java.time.temporal.ChronoUnit;
import java.util.Objects;
import lombok.EqualsAndHashCode;
import lombok.Value;
import westmeijer.oskar.shared.model.response.ClientLogger;

@Value
@EqualsAndHashCode(callSuper = true)
public class ClientActivity extends HistorizedEvent implements ClientLogger {

  ClientDetails clientDetails;

  public ClientActivity(HistorizedEventType event, ClientDetails clientDetails) {
    super(event);
    this.clientDetails = Objects.requireNonNull(clientDetails, "clientDetails is required");
  }

  @Override
  public String getClientLog() {
    return String.format("Client: %s, event: %s", clientDetails.getTag(), super.getEvent());
  }

  @Override
  public String getHistorizedLog() {
    return String.format("%s activity: %s %s", super.getRecordedAt().truncatedTo(ChronoUnit.SECONDS), clientDetails.getTag(),
        super.getEvent());
  }
}
