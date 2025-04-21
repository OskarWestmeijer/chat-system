package westmeijer.oskar.shared.model.response;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class RelayedClientActivity extends ServerMessage implements Serializable, ClientLogger {

  @Serial
  private static final long serialVersionUID = 57867855L;

  String clientTag;

  ActivityType type;

  Instant recordedAt;

  @Override
  public String getClientLog() {
    return "%s activity: %s %s".formatted(recordedAt.truncatedTo(ChronoUnit.SECONDS), clientTag, type);
  }

  public enum ActivityType {
    CONNECTED,
    DISCONNECTED
  }

}
