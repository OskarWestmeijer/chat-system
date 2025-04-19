package westmeijer.oskar.shared.model;

import static org.assertj.core.api.BDDAssertions.then;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.Instant;
import java.util.UUID;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import westmeijer.oskar.shared.model.history.ClientMessage;
import westmeijer.oskar.shared.model.history.HistoryEventType;

class ClientMessageTest {

  @Test
  @SneakyThrows
  void shouldSerializeAndDeserializeChatMessageDto() {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(bos);

    var message = "Hello";
    var client = new ClientDetails(UUID.randomUUID(), "ip", Instant.now());
    var original = new ClientMessage(message, client);
    oos.writeObject(original);
    oos.close();

    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
    ClientMessage copy = (ClientMessage) ois.readObject();

    then(copy)
        .extracting("message", "event")
        .containsExactly(message, HistoryEventType.CHAT_MESSAGE_RECEIVED);
  }


}