package de.schlaumeijer.service.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ClientConnectionDto implements Serializable {

  @Serial
  private static final long serialVersionUID = 99141992L;

  private UUID id;

  private String ip;

  private Instant connectedAt;

}
