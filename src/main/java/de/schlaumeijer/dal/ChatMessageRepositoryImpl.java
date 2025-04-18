package de.schlaumeijer.dal;

import de.schlaumeijer.bl.ChatMessageRepository;
import de.schlaumeijer.shared.ChatMessageDto;
import java.util.ArrayList;
import java.util.List;


public class ChatMessageRepositoryImpl implements ChatMessageRepository {

  private final ChatMessageMapper chatMessageMapper = new ChatMessageMapper();
  private final List<ChatMessageEntity> chatMessageEntities = new ArrayList<>();

  @Override
  public void insertMessage(ChatMessageDto chatMessageDto) {
    ChatMessageEntity chatMessageEntity = chatMessageMapper.mapToEntity(chatMessageDto);
    chatMessageEntities.add(chatMessageEntity);
  }

  @Override
  public List<ChatMessageDto> readAllMessages() {
    return chatMessageMapper.mapToDtoList(chatMessageEntities);
  }
}
