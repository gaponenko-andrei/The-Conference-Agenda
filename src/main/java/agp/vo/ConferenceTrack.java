package agp.vo;

import static com.google.common.base.Preconditions.*;
import static com.google.common.base.Preconditions.checkArgument;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Iterables;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Delegate;
import lombok.val;


@Value
@EqualsAndHashCode(callSuper = true)
public final class ConferenceTrack extends EventSequence<ConferenceTrack.ScheduledEvent> {

  public static final LocalTime START_TIME = LocalTime.of(9, 0);


  private ConferenceTrack(List<ScheduledEvent> events) {
    super("Conference track", events);
  }

  public Set<Talk> talks() {
    return this.stream()
      .map(ScheduledEvent::delegate)
      .filter(Talk.class::isInstance)
      .map(Talk.class::cast)
      .collect(Collectors.toSet());
  }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
  /*                             Builder related stuff                               */
  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {

    private List<ScheduledEvent> scheduledEvents = new ArrayList<>();

    public Builder scheduleSequence(EventSequence<? extends Event> eventSequence) {
      eventSequence.forEach(this::scheduleEvent);
      return this;
    }

    public Builder scheduleEvent(Event event) {
      requireNonSequence(event);
      if (scheduledEvents.isEmpty()) {
        scheduleFirst(event);
      } else {
        scheduleAfterLast(event);
      }
      return this;
    }

    private void scheduleFirst(Event event) {
      checkState(scheduledEvents.isEmpty(), "This should've never happened.");
      scheduledEvents.add(new ScheduledEvent(START_TIME, event));
    }

    private void scheduleAfterLast(Event event) {
      val lastEvent = Iterables.getLast(scheduledEvents);
      scheduledEvents.add(new ScheduledEvent(lastEvent.endTime(), event));
    }

    private static void requireNonSequence(Event event) {
      checkArgument(!(event instanceof EventSequence), "EventSequence is not allowed here.");
    }

    public ConferenceTrack build() {
      return new ConferenceTrack(scheduledEvents);
    }
  }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
  /*                                Scheduled Event                                  */
  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

  @Value
  public static final class ScheduledEvent implements Event {

    @NonNull
    private final LocalTime startTime;

    @Delegate @NonNull
    private final Event delegate;


    public LocalTime endTime() {
      return startTime.plus(delegate.duration());
    }
  }
}
