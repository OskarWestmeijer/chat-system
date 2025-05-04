package westmeijer.oskar.server.service;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import westmeijer.oskar.server.client.ClientListener;
import westmeijer.oskar.server.service.model.ClientActivity;
import westmeijer.oskar.server.service.model.ClientDetails;
import westmeijer.oskar.server.service.model.ClientMessage;
import westmeijer.oskar.shared.model.response.RelayedChatMessage;
import westmeijer.oskar.shared.model.response.RelayedClientActivity;
import westmeijer.oskar.shared.model.response.RelayedClientActivity.ActivityType;
import westmeijer.oskar.shared.model.response.ServerMessage;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OutgoingNotificationService {

  public static void notifyClientActivity(List<ClientListener> clients, ClientActivity clientActivity, ActivityType activityType) {
    var clientDetails = clientActivity.getClientDetails();
    var relayedActivity = new RelayedClientActivity(clientDetails.getTag(), activityType, clientDetails.getConnectedAt());
    clients.forEach(client -> sendMessage(client, relayedActivity));
  }

  public static void notifyChatMessage(List<ClientListener> audience, ClientDetails author, ClientMessage message) {
    var relayedMessage = new RelayedChatMessage(author.getTag(), message.getMessage());
    audience.forEach(client -> sendMessage(client, relayedMessage));
  }

  public static void sendMessage(ClientListener receiver, ServerMessage message) {
    receiver.getClientStreamProvider().writeToStream(message);
  }

}
