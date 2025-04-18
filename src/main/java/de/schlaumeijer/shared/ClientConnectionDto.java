package de.schlaumeijer.shared;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientConnectionDto implements Serializable {

  private UUID uuid;

  private String ipAdress;

  private String name;

  private Date connectionDate;

  private Date disconnectionDate;

}
