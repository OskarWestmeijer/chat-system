import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Data
public class ChatMessageDto {

    private UUID uuid;

    private String senderIp;

    private String senderName;

    private String message;

    private Date date;


}
