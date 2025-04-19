package westmeijer.oskar.shared.model.history;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;

@Getter
public abstract class HistoryEvent implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  private final UUID id;
  private final Instant recordedAt;
  private final HistoryEventType event;

  public HistoryEvent(HistoryEventType event) {
    this.id = UUID.randomUUID();
    this.recordedAt = Instant.now();
    this.event = Objects.requireNonNull(event, "event is required");
  }
}
