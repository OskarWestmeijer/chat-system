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

class ChatMessageDtoTest {

  @Test
  @SneakyThrows
  void shouldSerializeAndDeserializeChatMessageDto() {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(bos);

    var message = "Hello";
    var client = new ClientConnectionDto(UUID.randomUUID(), "ip", Instant.now());
    var original = new ChatMessageDto(message, client);
    oos.writeObject(original);
    oos.close();

    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
    ChatMessageDto copy = (ChatMessageDto) ois.readObject();

    then(copy)
        .extracting("message", "event")
        .containsExactly(message, ServerEvent.CHAT_MESSAGE_RECEIVED);
  }


}