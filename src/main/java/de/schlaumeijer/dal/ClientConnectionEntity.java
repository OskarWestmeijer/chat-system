package de.schlaumeijer.dal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "client_connections")
public class ClientConnectionEntity {

    @Id
    @Type(type = "uuid-char")
    @Column(name = "uuid")
    private UUID uuid;

    @Column(name = "ip_adress")
    private String ipAdress;

    @Column(name = "name")
    private String name;

    @Column(name = "date_connected")
    private Date connectionDate;

    @Column(name = "date_disconnected")
    private Date disconnectionDate;

}
