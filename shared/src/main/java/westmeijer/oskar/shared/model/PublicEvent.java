package westmeijer.oskar.shared.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
public abstract class PublicEvent implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  private final UUID id;
  private final Instant recordedAt;
  private final ServerEvent event;

  public PublicEvent(ServerEvent event) {
    this.id = UUID.randomUUID();
    this.recordedAt = Instant.now();
    this.event = event;
  }
}
