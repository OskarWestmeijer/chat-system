package westmeijer.oskar.server.service;

import java.net.Socket;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import westmeijer.oskar.server.client.ClientListener;
import westmeijer.oskar.server.client.ClientProcessor;
import westmeijer.oskar.server.client.ClientStreamProvider;
import westmeijer.oskar.server.service.model.ClientDetails;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientInitializer {

  @Getter
  private static final ClientInitializer instance = new ClientInitializer();

  public ClientListener init(Socket clientSocket, HistorizedEventService history, ClientRegister register) {
    var details = ClientDetails.from(clientSocket.getInetAddress().getHostAddress());
    var outputStream = ClientStreamProvider.createOutput(clientSocket);
    var inputStream = ClientStreamProvider.createInput(clientSocket);
    var streams = new ClientStreamProvider(clientSocket, inputStream, outputStream);
    var processor = new ClientProcessor(history, register, details);

    return new ClientListener(history, register, streams, processor, details);
  }

}
