package de.schlaumeijer.dal;

import de.schlaumeijer.shared.ChatMessageDto;

import java.util.ArrayList;
import java.util.List;


public final class ChatMessageMapper {

    public ChatMessageDto mapToDto(ChatMessageEntity chatMessageEntity) {
        return new ChatMessageDto(chatMessageEntity.getUuid(), chatMessageEntity.getSenderIp(),
                chatMessageEntity.getSenderName(), chatMessageEntity.getMessage(), chatMessageEntity.getDate());
    }

    public ChatMessageEntity mapToEntity(ChatMessageDto chatMessageDto) {
        return new ChatMessageEntity(chatMessageDto.getUuid(), chatMessageDto.getSenderIp(),
                chatMessageDto.getSenderName(), chatMessageDto.getMessage(), chatMessageDto.getDate());
    }

    public List<ChatMessageDto> mapToDtoList(List<ChatMessageEntity> chatMessageEntityList) {
        List<ChatMessageDto> chatMessageDtoList = new ArrayList<>();
        for (ChatMessageEntity chatMessageEntity : chatMessageEntityList) {
            chatMessageDtoList.add(new ChatMessageDto(chatMessageEntity.getUuid(), chatMessageEntity.getSenderIp(),
                    chatMessageEntity.getSenderName(), chatMessageEntity.getMessage(), chatMessageEntity.getDate()));
        }
        return chatMessageDtoList;
    }

    public List<ChatMessageEntity> mapToEntityList(List<ChatMessageDto> chatMessageDtosList) {
        List<ChatMessageEntity> chatMessageEntityList = new ArrayList<>();
        for (ChatMessageDto chatMessageDto : chatMessageDtosList) {
            chatMessageEntityList.add(new ChatMessageEntity(chatMessageDto.getUuid(), chatMessageDto.getSenderIp(),
                    chatMessageDto.getSenderName(), chatMessageDto.getMessage(), chatMessageDto.getDate()));
        }
        return chatMessageEntityList;
    }

}
