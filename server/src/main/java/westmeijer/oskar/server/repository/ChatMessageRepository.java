package westmeijer.oskar.server.repository;

import java.util.List;
import westmeijer.oskar.shared.model.ChatMessageDto;

public interface ChatMessageRepository {

  void insertMessage(ChatMessageDto chatMessageDto);

  List<ChatMessageDto> readAllMessages();

}
