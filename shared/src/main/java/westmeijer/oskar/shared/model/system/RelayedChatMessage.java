package westmeijer.oskar.shared.model.system;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;
import lombok.Value;
import westmeijer.oskar.shared.model.ClientLogger;

@Value
public class RelayedChatMessage implements Serializable, ClientLogger {

  @Serial
  private static final long serialVersionUID = 554893455L;

  UUID clientId;

  String message;

  @Override
  public String getClientLog() {
    return "%s: %s".formatted(clientId, message);
  }
}
