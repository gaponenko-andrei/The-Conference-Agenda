package agp.util;

import java.time.Duration;
import java.util.Collection;
import java.util.Set;

import agp.vo.Event;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Events {

  public static <E extends Event> Duration durationOf(Collection<E> events) {
    return events.stream()
      .map(Event::duration)
      .reduce(Duration::plus)
      .orElse(Duration.ofMinutes(0));
  }

  public static <E extends Event> Set<Event> upcast(Set<E> events) {
    return Collections.mapIntoSet(events, Event.class::cast);
  }
}
