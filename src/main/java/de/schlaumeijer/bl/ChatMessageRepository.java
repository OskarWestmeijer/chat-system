package de.schlaumeijer.bl;

import de.schlaumeijer.shared.ChatMessageDto;
import java.util.List;

public interface ChatMessageRepository {

  void insertMessage(ChatMessageDto chatMessageDto);

  List<ChatMessageDto> readAllMessages();

}
