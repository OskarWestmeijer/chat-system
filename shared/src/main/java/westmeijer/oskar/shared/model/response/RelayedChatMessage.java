package westmeijer.oskar.shared.model.response;

import java.io.Serial;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class RelayedChatMessage extends ServerMessage implements Serializable, ClientLogger {

  @Serial
  private static final long serialVersionUID = 554893455L;

  String clientTag;

  String message;

  @Override
  public String getClientLog() {
    return "%s: %s".formatted(clientTag, message);
  }
}
