package de.schlaumeijer.dal;

import java.util.Date;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientConnectionEntity {

  private UUID uuid;

  private String ipAdress;

  private String name;

  private Date connectionDate;

  private Date disconnectionDate;

}
