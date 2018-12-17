package de.schlaumeijer.bl;

import de.schlaumeijer.shared.ChatMessageDto;
import de.schlaumeijer.shared.ClientConnectionDto;

import java.io.IOException;
import java.util.List;

public class MessageCtrl {

    private static MessageCtrl instance;

    private ClientCtrl clientCtrl;

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
            e.printStackTrace();
        }
    }

    public void listMessages(List<ChatMessageDto> chatMessageDtoList) {
        try {
            for (ChatMessageDto chatMessageDto : chatMessageDtoList) {
                clientCtrl.getObjectOutputStream().writeObject(chatMessageDto);
                clientCtrl.getObjectOutputStream().flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * currently active ones
     */
    public void listClients(List<ClientConnectionDto> clientConnectionDtoList) {

    }

    public void listClientHistory(List<ClientConnectionDto> clientConnectionDtoList){
        try {
            for (ClientConnectionDto clientConnectionDto : clientConnectionDtoList) {
                clientCtrl.getObjectOutputStream().writeObject(clientConnectionDto);
                clientCtrl.getObjectOutputStream().flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
