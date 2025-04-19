package westmeijer.oskar.shared.model.system;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ClientChatRequest implements Serializable {

  @Serial
  private static final long serialVersionUID = 4444L;

  String message;

  Instant sendAt;

}
