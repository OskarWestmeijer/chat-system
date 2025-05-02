package westmeijer.oskar.client.service;

import static org.assertj.core.api.BDDAssertions.assertThatThrownBy;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import lombok.SneakyThrows;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class InitializerFactoryTest {

  @Mock
  private Socket mockSocket;

  @Mock
  private ObjectInputStream mockInputStream;

  @Mock
  private ObjectOutputStream mockOutputStream;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void shouldCreateSocketException() {
    thenThrownBy(() -> InitializerFactory.createSocket(null, null))
        .isInstanceOf(RuntimeException.class);
  }

  @Test
  void shouldThrowExceptionWhenSocketCreationFails() throws IOException {
    // Given
    String host = "localhost";
    int port = 1234;
    willThrow(new IOException("Socket creation failed")).given(mockSocket).getInputStream();

    // When / Then
    assertThatThrownBy(() -> InitializerFactory.createSocket(host, port))
        .isInstanceOf(RuntimeException.class)
        .hasCauseInstanceOf(IOException.class);
  }

  @Test
  void shouldCreateObjectOutputStreamSuccessfully() throws IOException {
    // Given
    given(mockSocket.getOutputStream()).willReturn(mock(java.io.OutputStream.class));

    // When
    ObjectOutputStream outputStream = InitializerFactory.createOutput(mockSocket);

    // Then
    BDDAssertions.then(outputStream).isNotNull();
    then(mockSocket).should().getOutputStream();
  }

  @Test
  void shouldThrowExceptionWhenObjectInputStreamCreationFails() throws IOException {
    // Given
    willThrow(new IOException("InputStream creation failed")).given(mockSocket).getInputStream();

    // When / Then
    assertThatThrownBy(() -> InitializerFactory.createInput(mockSocket))
        .isInstanceOf(RuntimeException.class)
        .hasCauseInstanceOf(IOException.class);
  }

  @Test
  @SneakyThrows
  void shouldCreateStreamProviderSuccessfully() {
    // Given
    given(mockSocket.getInputStream()).willReturn(mock(java.io.InputStream.class));
    given(mockSocket.getOutputStream()).willReturn(mock(java.io.OutputStream.class));

    // When
    StreamProvider streamProvider = InitializerFactory.createStreamProvider(mockSocket, mockInputStream, mockOutputStream);

    // Then
    BDDAssertions.then(streamProvider).isNotNull();
  }
}