package de.schlaumeijer.bl;

import de.schlaumeijer.shared.ChatMessageDto;
import de.schlaumeijer.shared.ClientConnectionDto;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageCtrl {

  private static MessageCtrl instance;

  private final ClientCtrl clientCtrl;

  private MessageCtrl(ClientCtrl clientCtrl) {
    this.clientCtrl = clientCtrl;
  }

  public static synchronized MessageCtrl getInstance(ClientCtrl clientCtrl) {
    if (MessageCtrl.instance == null) {
      MessageCtrl.instance = new MessageCtrl(clientCtrl);
    }
    return MessageCtrl.instance;
  }

  public void sendMessageToOtherClients(ChatMessageDto chatMessageDto) {
    try {
      for (ClientCtrl client : ServerCtrl.CONNECTED_CLIENT_CTRLS) {
        client.getObjectOutputStream().writeObject(chatMessageDto);
        client.getObjectOutputStream().flush();
      }
    } catch (IOException e) {
      log.error("Exception thrown.", e);
    }
  }

  public void listMessages(List<ChatMessageDto> chatMessageDtoList) {
    try {
      for (ChatMessageDto chatMessageDto : chatMessageDtoList) {
        clientCtrl.getObjectOutputStream().writeObject(chatMessageDto);
        clientCtrl.getObjectOutputStream().flush();
      }
    } catch (IOException e) {
      log.error("Exception thrown.", e);
    }
  }

  public void listClientHistory(List<ClientConnectionDto> clientConnectionDtoList) {
    try {
      for (ClientConnectionDto clientConnectionDto : clientConnectionDtoList) {
        clientCtrl.getObjectOutputStream().writeObject(clientConnectionDto);
        clientCtrl.getObjectOutputStream().flush();
      }
    } catch (IOException e) {
      log.error("Exception thrown.", e);
    }
  }


}
