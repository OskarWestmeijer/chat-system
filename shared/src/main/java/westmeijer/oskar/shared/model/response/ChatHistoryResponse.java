package westmeijer.oskar.shared.model.response;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class ChatHistoryResponse extends ServerMessage implements Serializable {

  @Serial
  private static final long serialVersionUID = 55646L;

  List<String> messageHistory;

}
