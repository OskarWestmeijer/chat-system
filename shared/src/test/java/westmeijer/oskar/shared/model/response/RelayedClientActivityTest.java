package westmeijer.oskar.shared.model.response;

import static org.assertj.core.api.BDDAssertions.then;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.Instant;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import westmeijer.oskar.shared.model.response.RelayedClientActivity.ActivityType;

class RelayedClientActivityTest {

  @Test
  @SneakyThrows
  void shouldSerializeAndDeserialize() {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(bos);

    var tag = "#abc";
    var activity = ActivityType.CONNECTED;
    var recordedAt = Instant.now();
    var expected = new RelayedClientActivity(tag, activity, recordedAt);
    oos.writeObject(expected);
    oos.close();

    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
    var actual = (RelayedClientActivity) ois.readObject();

    then(actual)
        .extracting("clientTag", "type", "recordedAt")
        .containsExactly(tag, activity, recordedAt);
  }


}