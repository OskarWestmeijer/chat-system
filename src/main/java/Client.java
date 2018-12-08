import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class Client implements Runnable {

    private Socket socket;

    private InputStreamReader inputStreamReader;

    private BufferedReader bufferedReader;

    private PrintWriter printWriter;

    private boolean isConnected = true;

    private List<Client> connectedClients;

    public Client(Socket socket, List<Client> connectedClients) {
        try {
            this.connectedClients = connectedClients;
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
                System.out.println("Received msg: " + message);
                if (message.equals("q!"))
                    disconnect();
                else
                    for (Client c : connectedClients) {
                        c.getPrintWriter().println(message);
                        c.getPrintWriter().flush();
                    }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void disconnect() {
        try {
            bufferedReader.close();
            inputStreamReader.close();
            printWriter.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PrintWriter getPrintWriter() {
        return printWriter;
    }

}
