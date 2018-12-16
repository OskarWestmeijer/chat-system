import org.junit.Assert;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import java.util.Date;
import java.util.UUID;

public class ChatMessageMapperTest {

    private ChatMessageMapper chatMessageMapper = Mappers.getMapper(ChatMessageMapper.class);

    @Test
    public void mapDtoToEntity() {
       ChatMessageDto chatMessageDto = new ChatMessageDto();
        chatMessageDto.setUuid(UUID.randomUUID());
        chatMessageDto.setDate(new Date(System.currentTimeMillis()));
        chatMessageDto.setMessage("HelloFromMe");
        chatMessageDto.setSenderName("Blug");
        chatMessageDto.setSenderIp("localhost");
        ChatMessageEntity chatMessageEntity = chatMessageMapper.mapToEntity(chatMessageDto);
        Assert.assertEquals(chatMessageEntity.getMessage(), chatMessageDto.getMessage());
        Assert.assertEquals(chatMessageEntity.getSenderName(), chatMessageDto.getSenderName());
        Assert.assertEquals(chatMessageEntity.getSenderIp(), chatMessageDto.getSenderIp());
        Assert.assertEquals(chatMessageEntity.getUuid(), chatMessageDto.getUuid());
        Assert.assertEquals(chatMessageEntity.getDate(), chatMessageDto.getDate());
    }

}
