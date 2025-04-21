package westmeijer.oskar.server.client;

import java.net.Socket;
import westmeijer.oskar.server.service.model.ClientDetails;

public class ClientFactory {

  public static ClientListener create(Socket socket, ClientDetails clientDetails) {
    return new ClientListener(socket, clientDetails);
  }

}
