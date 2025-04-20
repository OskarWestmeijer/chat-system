package westmeijer.oskar.shared.model.response;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import lombok.Value;

@Value
public class RelayedClientActivity implements Serializable, ClientLogger {

  @Serial
  private static final long serialVersionUID = 57867855L;

  UUID clientId;

  ACTIVITY_TYPE type;

  Instant recordedAt;

  @Override
  public String getClientLog() {
    return "%s %s: %s".formatted(recordedAt.truncatedTo(ChronoUnit.SECONDS), clientId, type);
  }

  public static enum ACTIVITY_TYPE {
    CONNECTED,
    DISCONNECTED
  }

}
