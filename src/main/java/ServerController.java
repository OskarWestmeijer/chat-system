import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerController {

    private Socket socket;

    private ServerSocket server;

    private boolean isListening = false;

    private List<Client> connectedClients = new ArrayList<>();

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

    private void newClientConnection(Socket socket){
        System.out.println("New Client joined.");
        Client client = new Client(socket, connectedClients);
        connectedClients.add(client);
        Thread thread = new Thread(client);
        thread.start();
    }

}