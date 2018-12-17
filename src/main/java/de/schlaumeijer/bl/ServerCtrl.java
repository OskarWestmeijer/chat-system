package de.schlaumeijer.bl;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerCtrl {

    private ServerSocket server;

    private boolean isListening = true;

    public static final List<ClientCtrl> CONNECTED_CLIENT_CTRLS = new ArrayList<>();

    public ServerCtrl(int port) {
        System.out.println("Created Server Ctrl at port: "+port);
        try {
            this.server = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listenForConnection() {
        try {
            while (isListening) {
                newClientConnection(server.accept());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void newClientConnection(Socket socket) {
        System.out.println("New de.schlaumeijer.bl.ClientCtrl " + socket.getInetAddress() + " joined.");
        ClientCtrl clientCtrl = new ClientCtrl(socket);
        CONNECTED_CLIENT_CTRLS.add(clientCtrl);
        Thread thread = new Thread(clientCtrl);
        thread.start();
    }

}