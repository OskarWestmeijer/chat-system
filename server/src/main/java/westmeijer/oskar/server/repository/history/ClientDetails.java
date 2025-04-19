package westmeijer.oskar.server.repository.history;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import westmeijer.oskar.shared.model.response.ClientLogger;

@Value
@Builder
@AllArgsConstructor
public class ClientDetails implements Serializable, ClientLogger {

  @Serial
  private static final long serialVersionUID = 99141992L;

  UUID id;

  String ip;

  Instant connectedAt;

  public static ClientDetails from(String clientIp) {
    return ClientDetails.builder()
        .id(UUID.randomUUID())
        .ip(clientIp)
        .connectedAt(Instant.now())
        .build();
  }

  @Override
  public String getClientLog() {
    return "clientId: %s, connectedAt: %s".formatted(id, connectedAt.truncatedTo(ChronoUnit.SECONDS));
  }
}
