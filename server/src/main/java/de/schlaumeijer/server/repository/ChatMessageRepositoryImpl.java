package de.schlaumeijer.server.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import westmeijer.oskar.shared.model.ChatMessageDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatMessageRepositoryImpl implements ChatMessageRepository {

  private final List<ChatMessageDto> chatMessages = new ArrayList<>();

  private static ChatMessageRepositoryImpl instance;

  public static synchronized ChatMessageRepositoryImpl getInstance() {
    if (ChatMessageRepositoryImpl.instance == null) {
      ChatMessageRepositoryImpl.instance = new ChatMessageRepositoryImpl();
    }
    return ChatMessageRepositoryImpl.instance;
  }

  @Override
  public void insertMessage(ChatMessageDto chatMessageDto) {
    Objects.requireNonNull(chatMessageDto, "chat message is required");
    chatMessages.add(chatMessageDto);
  }

  @Override
  public List<ChatMessageDto> readAllMessages() {
    return List.copyOf(chatMessages);
  }
}
