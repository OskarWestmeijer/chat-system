package westmeijer.oskar.server.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import westmeijer.oskar.shared.model.history.HistoryEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PublicEventHistoryRepository {

  private final List<HistoryEvent> history = new ArrayList<>();

  private static PublicEventHistoryRepository instance;

  public static synchronized PublicEventHistoryRepository getInstance() {
    if (PublicEventHistoryRepository.instance == null) {
      PublicEventHistoryRepository.instance = new PublicEventHistoryRepository();
    }
    return PublicEventHistoryRepository.instance;
  }

  public void insertMessage(HistoryEvent historyEvent) {
    Objects.requireNonNull(historyEvent, "publicEvent is required");
    history.add(historyEvent);
  }

  public List<HistoryEvent> getHistory() {
    return List.copyOf(history);
  }

  public void clearHistory() {
    history.clear();
  }
}
