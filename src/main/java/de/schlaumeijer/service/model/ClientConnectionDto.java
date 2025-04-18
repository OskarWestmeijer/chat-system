package de.schlaumeijer.service.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class ClientConnectionDto implements Serializable {

  @Serial
  private static final long serialVersionUID = 99141992L;

  UUID id;

  String ip;

  Instant connectedAt;

  public static ClientConnectionDto from(String clientIp) {
    return ClientConnectionDto.builder()
        .id(UUID.randomUUID())
        .ip(clientIp)
        .connectedAt(Instant.now())
        .build();
  }

}
