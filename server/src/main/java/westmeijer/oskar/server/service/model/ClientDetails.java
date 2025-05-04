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

  public static ClientDetails from(String clientIp) {
    return ClientDetails.builder()
        .id(UUID.randomUUID())
        .tag(generateUniqueTag())
        .ip(clientIp)
        .connectedAt(Instant.now())
        .build();
  }

  private static String generateUniqueTag() {
    return "#" + UUID.randomUUID().toString().substring(0, 3);
  }

  @Override
  public String getClientLog() {
    // TODO: is this required? check.
    return "client: %s, connectedAt: %s".formatted(tag, connectedAt.truncatedTo(ChronoUnit.SECONDS));
  }
}
