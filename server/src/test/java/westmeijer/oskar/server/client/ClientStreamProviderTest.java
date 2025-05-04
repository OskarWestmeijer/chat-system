package westmeijer.oskar.server.client;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClientStreamProviderTest {

  private Socket socket;
  private ObjectInputStream inputStream;
  private ObjectOutputStream outputStream;
  private ClientStreamProvider streamProvider;

  @BeforeEach
  void setUp() {
    socket = mock(Socket.class);
    inputStream = mock(ObjectInputStream.class);
    outputStream = mock(ObjectOutputStream.class);
    streamProvider = new ClientStreamProvider(socket, inputStream, outputStream);
  }

  @Test
  void shouldWriteToStream() throws Exception {
    var message = "test-message";

    streamProvider.writeToStream(message);

    verify(outputStream).writeObject(message);
    verify(outputStream).flush();
  }

  @Test
  void shouldReadFromStream() throws Exception {
    var expected = "incoming-message";
    given(inputStream.readObject()).willReturn(expected);

    var result = streamProvider.readFromStream();

    then(result).isEqualTo(expected);
  }

  @Test
  void shouldCloseStreams() throws Exception {
    streamProvider.closeStreams();

    verify(inputStream).close();
    verify(outputStream).close();
    verify(socket).close();
  }

  @Test
  void shouldCreateOutput() throws Exception {
    var socket = mock(Socket.class);
    var byteOut = new java.io.ByteArrayOutputStream();

    when(socket.getOutputStream()).thenReturn(byteOut);

    var result = ClientStreamProvider.createOutput(socket);

    then(result).isNotNull();
    result.writeObject("test");  // optional write test
    result.flush();
  }


  @Test
  void writeToStream_shouldThrowRuntimeException_whenWriteFails() throws Exception {
    var socket = mock(Socket.class);
    var inputStream = mock(ObjectInputStream.class);
    var outputStream = mock(ObjectOutputStream.class);

    var provider = new ClientStreamProvider(socket, inputStream, outputStream);

    doThrow(new IOException("Write error")).when(outputStream).writeObject(any());

    assertThatThrownBy(() -> provider.writeToStream("test"))
        .isInstanceOf(RuntimeException.class)
        .hasCauseInstanceOf(IOException.class);
  }

  @Test
  void writeToStream_shouldThrowRuntimeException_whenFlushFails() throws Exception {
    var socket = mock(Socket.class);
    var inputStream = mock(ObjectInputStream.class);
    var outputStream = mock(ObjectOutputStream.class);

    var provider = new ClientStreamProvider(socket, inputStream, outputStream);

    // writeObject works fine, flush throws
    doNothing().when(outputStream).writeObject(any());
    doThrow(new IOException("Flush error")).when(outputStream).flush();

    assertThatThrownBy(() -> provider.writeToStream("test"))
        .isInstanceOf(RuntimeException.class)
        .hasCauseInstanceOf(IOException.class);
  }

  @Test
  void readFromStream_shouldThrowRuntimeException_whenReadFails() throws Exception {
    var socket = mock(Socket.class);
    var inputStream = mock(ObjectInputStream.class);
    var outputStream = mock(ObjectOutputStream.class);

    var provider = new ClientStreamProvider(socket, inputStream, outputStream);

    when(inputStream.readObject()).thenThrow(new IOException("Read error"));

    assertThatThrownBy(provider::readFromStream)
        .isInstanceOf(RuntimeException.class)
        .hasCauseInstanceOf(IOException.class);
  }

  @Test
  void closeStreams_shouldNotThrow_whenCloseThrows() throws Exception {
    var socket = mock(Socket.class);
    var inputStream = mock(ObjectInputStream.class);
    var outputStream = mock(ObjectOutputStream.class);

    doThrow(new IOException("input close fail")).when(inputStream).close();
    doThrow(new IOException("output close fail")).when(outputStream).close();
    doThrow(new IOException("socket close fail")).when(socket).close();

    var provider = new ClientStreamProvider(socket, inputStream, outputStream);

    // no exception should be thrown
    provider.closeStreams();

    // verify close was attempted on all
    verify(inputStream).close();
    verify(outputStream).close();
    verify(socket).close();
  }

  @Test
  void createOutput_shouldThrowRuntimeException_whenGetOutputStreamFails() throws Exception {
    var socket = mock(Socket.class);
    when(socket.getOutputStream()).thenThrow(new IOException("getOutputStream failed"));

    assertThatThrownBy(() -> ClientStreamProvider.createOutput(socket))
        .isInstanceOf(RuntimeException.class)
        .hasCauseInstanceOf(IOException.class);
  }

  @Test
  void createInput_shouldThrowRuntimeException_whenGetInputStreamFails() throws Exception {
    var socket = mock(Socket.class);
    when(socket.getInputStream()).thenThrow(new IOException("getInputStream failed"));

    assertThatThrownBy(() -> ClientStreamProvider.createInput(socket))
        .isInstanceOf(RuntimeException.class)
        .hasCauseInstanceOf(IOException.class);
  }

}
