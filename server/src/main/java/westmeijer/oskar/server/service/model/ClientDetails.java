package westmeijer.oskar.server.service.model;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;
import westmeijer.oskar.shared.model.response.ClientLogger;

@Value
@Builder
public class ClientDetails implements ClientLogger {

  UUID id;

  String tag;

  String ip;

  Instant connectedAt;

  public static ClientDetails from(String clientIp, String tag) {
    return ClientDetails.builder()
        .id(UUID.randomUUID())
        .tag(tag)
        .ip(clientIp)
        .connectedAt(Instant.now())
        .build();
  }

  @Override
  public String getClientLog() {
    // TODO: is this required? check.
    return "client: %s, connectedAt: %s".formatted(tag, connectedAt.truncatedTo(ChronoUnit.SECONDS));
  }
}
