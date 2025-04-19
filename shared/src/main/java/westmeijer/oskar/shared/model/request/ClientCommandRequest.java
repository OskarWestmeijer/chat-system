package westmeijer.oskar.shared.model.request;

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

  EventType eventType;

  Instant sendAt;

}
