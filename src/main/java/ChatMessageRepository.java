import java.util.List;
import java.util.UUID;

public interface ChatMessageRepository {

    void insertMessage(ChatMessageDto chatMessageDto);

    List<ChatMessageDto> readAllMessagesByIp(String ip);

    List<ChatMessageDto> readAllMessagesByName(String name);

    List<ChatMessageDto> readAllMessages();

}
