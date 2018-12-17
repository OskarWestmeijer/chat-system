package de.schlaumeijer.bl;

import de.schlaumeijer.dal.ChatMessageRepositoryImpl;
import de.schlaumeijer.dal.ClientConnectionRepositoryImpl;
import de.schlaumeijer.shared.ChatMessageDto;
import de.schlaumeijer.shared.ClientConnectionDto;
import lombok.Getter;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.UUID;

public class ClientCtrl implements Runnable {

    ChatMessageRepository chatMessageRepository;

    ClientConnectionRepository clientConnectionRepository;

    private Socket socket;

    private InputStream inputStream;

    private ObjectInputStream objectInputStream;

    private OutputStream outputStream;

    @Getter
    private ObjectOutputStream objectOutputStream;

    private boolean isConnected = true;

    private MessageCtrl messageCtrl;

    public ClientCtrl(Socket socket) {
        try {
            this.socket = socket;
            this.outputStream = socket.getOutputStream();
            this.objectOutputStream = new ObjectOutputStream(outputStream);
            this.objectOutputStream.flush();
            this.inputStream = socket.getInputStream();
            this.objectInputStream = new ObjectInputStream(inputStream);
            this.messageCtrl = MessageCtrl.getInstance(this);
            this.clientConnectionRepository = new ClientConnectionRepositoryImpl();
            this.chatMessageRepository = new ChatMessageRepositoryImpl();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            Object message;
            System.out.println("waiting for messages");
            while (isConnected && (message = objectInputStream.readObject()) != null) {
                System.out.println("Received Message from : " + socket.getInetAddress());
                processInput(message);
            }
        } catch (IOException e) {
            disconnect();
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void processInput(Object object) {
        if (object instanceof ChatMessageDto) {
            processChatMessageDto(((ChatMessageDto) object));
        } else if (object instanceof ClientConnectionDto) {
            processClientConnectionDto(((ClientConnectionDto) object));
        }

    }

    private void processChatMessageDto(ChatMessageDto chatMessageDto) {
        chatMessageDto.setDate(new Date(System.currentTimeMillis()));
        chatMessageDto.setUuid(UUID.randomUUID());
        chatMessageDto.setSenderIp(socket.getInetAddress().getHostAddress());
        chatMessageDto.setSenderName("Bob");
        chatMessageRepository.insertMessage(chatMessageDto);
        switch (chatMessageDto.getMessage()) {
            case "q!":
                disconnect();
                break;
            case "list -c":
                listClients();
                break;
            case "list -ch":
                listClientsHistory();
                break;
            case "list -m":
                listMessages();
                break;
            default:
                sendMessageToOtherClients(chatMessageDto);
        }
    }

    private void sendMessageToOtherClients(ChatMessageDto chatMessageDto) {
        messageCtrl.sendMessageToOtherClients(chatMessageDto);
    }

    private void listMessages() {
        messageCtrl.listMessages(chatMessageRepository.readAllMessages());
    }

    private void listClientsHistory() {
        messageCtrl.listClientHistory(clientConnectionRepository.readHistoryOfConnections());
    }

    private void listClients() {
        //    messageCtrl.listClients(ServerCtrl.CONNECTED_CLIENT_CTRLS);

    }

    private void processClientConnectionDto(ClientConnectionDto clientConnectionDto) {

    }


    public void disconnect() {
        try {
            System.out.println("de.schlaumeijer.bl.ClientCtrl: " + socket.getInetAddress() + " left the chat.");
            isConnected = false;
            objectInputStream.close();
            inputStream.close();
            objectOutputStream.close();
            outputStream.close();
            socket.close();
            ServerCtrl.CONNECTED_CLIENT_CTRLS.remove(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
