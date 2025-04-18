import de.schlaumeijer.dal.ChatMessageEntity;
import de.schlaumeijer.dal.ChatMessageMapper;
import de.schlaumeijer.shared.ChatMessageDto;
import java.util.Date;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;

public class ChatMessageMapperTest {

  private final ChatMessageMapper chatMessageMapper = new ChatMessageMapper();

  @Test
  public void mapDtoToEntity() {
    ChatMessageDto chatMessageDto = createChatMessageDto();
    ChatMessageEntity chatMessageEntity = chatMessageMapper.mapToEntity(chatMessageDto);
    Assert.assertEquals(chatMessageEntity.getMessage(), chatMessageDto.getMessage());
    Assert.assertEquals(chatMessageEntity.getSenderName(), chatMessageDto.getSenderName());
    Assert.assertEquals(chatMessageEntity.getSenderIp(), chatMessageDto.getSenderIp());
    Assert.assertEquals(chatMessageEntity.getUuid(), chatMessageDto.getUuid());
    Assert.assertEquals(chatMessageEntity.getDate(), chatMessageDto.getDate());
  }

  private ChatMessageDto createChatMessageDto() {
    ChatMessageDto chatMessageDto = new ChatMessageDto();
    chatMessageDto.setUuid(UUID.randomUUID());
    chatMessageDto.setDate(new Date(System.currentTimeMillis()));
    chatMessageDto.setMessage("HelloFromMe");
    chatMessageDto.setSenderName("Blug");
    chatMessageDto.setSenderIp("localhost");
    return chatMessageDto;
  }

}
