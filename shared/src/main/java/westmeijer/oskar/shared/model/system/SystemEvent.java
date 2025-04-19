package westmeijer.oskar.shared.model.system;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import lombok.Builder;
import lombok.Value;
import westmeijer.oskar.shared.model.ClientLogger;

@Value
@Builder
public class SystemEvent implements Serializable, ClientLogger {

  @Serial
  private static final long serialVersionUID = 25L;

  SystemEventType type;

  Instant recordedAt;

  @Override
  public String getClientLog() {
    return "Received event from server: %s".formatted(type);
  }
}
