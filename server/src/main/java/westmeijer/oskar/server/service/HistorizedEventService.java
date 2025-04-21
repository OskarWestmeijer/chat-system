package westmeijer.oskar.server.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import westmeijer.oskar.server.service.model.HistorizedEvent;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HistorizedEventService {

  private final List<HistorizedEvent> history = new ArrayList<>();

  private static HistorizedEventService instance;

  public static synchronized HistorizedEventService getInstance() {
    if (HistorizedEventService.instance == null) {
      HistorizedEventService.instance = new HistorizedEventService();
    }
    return HistorizedEventService.instance;
  }

  public void recordMessage(HistorizedEvent historizedEvent) {
    Objects.requireNonNull(historizedEvent, "historizedEvent is required");
    history.add(historizedEvent);
    log.info("Historized event: {}", historizedEvent);
  }

  public List<HistorizedEvent> getHistory() {
    return List.copyOf(history);
  }

  public void clearHistory() {
    history.clear();
  }
}
