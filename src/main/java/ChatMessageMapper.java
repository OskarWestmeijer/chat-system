import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ChatMessageMapper {

    ChatMessageMapper INSTANCE = Mappers.getMapper(ChatMessageMapper.class);

    ChatMessageDto mapToDt(ChatMessageEntity chatMessageEntity);

    ChatMessageEntity mapToEntity(ChatMessageDto chatMessageDto);

    List<ChatMessageDto> mapToDtoList(List<ChatMessageEntity> chatMessageEntity);

    List<ChatMessageEntity> mapToEntityList(List<ChatMessageDto> chatMessageDtos);

}
