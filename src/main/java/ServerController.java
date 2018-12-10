import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerController {

    private Socket socket;

    private ServerSocket server;

    private boolean isListening = false;

    public static final List<Client> CONNECTED_CLIENTS = new ArrayList<>();

    public ServerController(int port) {
        System.out.println("Created Server Ctrl");
        try {
            this.server = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listenForConnection() {
        try {
            isListening = true;

            while (isListening) {
                newClientConnection(server.accept());

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void newClientConnection(Socket socket) {
        System.out.println("New Client " + socket.getInetAddress() + " joined.");
        Client client = new Client(socket, CONNECTED_CLIENTS);
        CONNECTED_CLIENTS.add(client);
        Thread thread = new Thread(client);
        thread.start();
        sendMessageToClients("New Client " + socket.getInetAddress() + " joined.");
    }

    private void sendMessageToClients(String message) {
        for (Client c : ServerController.CONNECTED_CLIENTS) {
            c.getPrintWriter().println(socket.getInetAddress() + " says: " + message);
            c.getPrintWriter().flush();
        }
    }

}