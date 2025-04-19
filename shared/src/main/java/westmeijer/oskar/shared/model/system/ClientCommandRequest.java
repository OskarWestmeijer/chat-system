package westmeijer.oskar.shared.model.system;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ClientCommandRequest implements Serializable {

  @Serial
  private static final long serialVersionUID = 555L;

  SystemEventType eventType;

  Instant sendAt;

}
