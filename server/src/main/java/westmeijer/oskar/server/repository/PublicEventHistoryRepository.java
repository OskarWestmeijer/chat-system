package westmeijer.oskar.server.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import westmeijer.oskar.shared.model.PublicEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PublicEventHistoryRepository {

  private final List<PublicEvent> history = new ArrayList<>();

  private static PublicEventHistoryRepository instance;

  public static synchronized PublicEventHistoryRepository getInstance() {
    if (PublicEventHistoryRepository.instance == null) {
      PublicEventHistoryRepository.instance = new PublicEventHistoryRepository();
    }
    return PublicEventHistoryRepository.instance;
  }

  public void insertMessage(PublicEvent publicEvent) {
    Objects.requireNonNull(publicEvent, "publicEvent is required");
    history.add(publicEvent);
  }

  public List<PublicEvent> getHistory() {
    return List.copyOf(history);
  }

  public void clearHistory() {
    history.clear();
  }
}
