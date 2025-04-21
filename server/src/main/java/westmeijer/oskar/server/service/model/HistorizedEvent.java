package westmeijer.oskar.server.service.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;

@Getter
public abstract class HistorizedEvent {

  private final UUID id;
  private final Instant recordedAt;
  private final HistorizedEventType event;

  public HistorizedEvent(HistorizedEventType event) {
    this.id = UUID.randomUUID();
    this.recordedAt = Instant.now();
    this.event = Objects.requireNonNull(event, "event is required");
  }

  abstract public String getHistorizedLog();
}
