package de.schlaumeijer.repository;

import de.schlaumeijer.service.model.ChatMessageDto;
import java.util.List;

public interface ChatMessageRepository {

  void insertMessage(ChatMessageDto chatMessageDto);

  List<ChatMessageDto> readAllMessages();

}
