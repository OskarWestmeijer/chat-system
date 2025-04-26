package westmeijer.oskar.client.service;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

class OutputRequestTest {

  @Test
  @SneakyThrows
  void send_shouldWriteObjectToStream() {
    // Given
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
    String testObject = "Hello World";

    // When
    OutputRequest.send(objectOutputStream, testObject);

    // Then
    ObjectInputStream objectInputStream = new ObjectInputStream(
        new java.io.ByteArrayInputStream(byteArrayOutputStream.toByteArray())
    );
    Object readObject = objectInputStream.readObject();

    then(readObject)
        .isInstanceOf(String.class)
        .isEqualTo(testObject);
  }

  @Test
  void send_shouldThrowRuntimeException_whenWriteFails_mocked() throws Exception {
    // Given
    ObjectOutputStream mockOutputStream = mock(ObjectOutputStream.class);
    String testObject = "Hello Mock";

    doThrow(new IOException("Simulated IO error"))
        .when(mockOutputStream)
        .writeObject(testObject);

    // When / Then
    thenThrownBy(() -> OutputRequest.send(mockOutputStream, testObject))
        .isInstanceOf(RuntimeException.class)
        .hasCauseInstanceOf(IOException.class)
        .hasMessageContaining("java.io.IOException");
  }

}