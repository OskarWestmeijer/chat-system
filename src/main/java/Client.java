import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class Client implements Runnable {

    ChatMessageRepository chatMessageRepository;

    private Socket socket;

    private InputStreamReader inputStreamReader;

    private BufferedReader bufferedReader;

    private PrintWriter printWriter;

    private boolean isConnected = true;

    public Client(Socket socket) {
        try {
            this.chatMessageRepository = new ChatMessageRepositoryImpl();
            this.socket = socket;
            this.inputStreamReader = new InputStreamReader(socket.getInputStream());
            this.bufferedReader = new BufferedReader(inputStreamReader);
            this.printWriter = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String message;
            System.out.println("waiting for msg");
            while (isConnected && (message = bufferedReader.readLine()) != null) {
                chatMessageRepository.insertMessage(null);
                System.out.println("Received from "+socket.getInetAddress()+ " : " + message);
                if (message.equals("q!"))
                    disconnect();
                else
                    for (Client c : ServerController.CONNECTED_CLIENTS) {
                        c.getPrintWriter().println(socket.getInetAddress()+" says: " + message);
                        c.getPrintWriter().flush();
                    }
            }
        } catch (IOException e) {
            disconnect();
            e.printStackTrace();
        }
    }


    public void disconnect() {
        try {
            System.out.println("Client: "+ socket.getInetAddress()+" left the chat.");
            isConnected = false;
            bufferedReader.close();
            inputStreamReader.close();
            printWriter.close();
            socket.close();
            ServerController.CONNECTED_CLIENTS.remove(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PrintWriter getPrintWriter() {
        return printWriter;
    }

}
