package agp.vo;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.singleton;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import com.google.common.collect.ForwardingCollection;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import agp.util.Events;
import lombok.Getter;
import lombok.NonNull;

@Getter
public abstract class EventSequence<E extends Event> extends ForwardingCollection<E> implements Event {

  @NonNull
  private final String title;

  @NonNull
  private final Duration duration;

  @NonNull
  private final ImmutableCollection<E> events;


  EventSequence(@NonNull final String title, @NonNull final List<E> events) {
    this(title, (ImmutableCollection<E>) ImmutableList.copyOf(events));
  }

  EventSequence(@NonNull final String title, @NonNull final Set<E> events) {
    this(title, (ImmutableCollection<E>) ImmutableSet.copyOf(events));
  }

  private EventSequence(final String title, final ImmutableCollection<E> events) {
    requireNonEmpty(events);
    this.title = title;
    this.events = events;
    this.duration = Events.durationOf(events);
  }

  private static void requireNonEmpty(Collection<?> events) {
    checkArgument(!events.isEmpty(), "EventSequence must have events.");
  }

  @Override
  protected ImmutableCollection<E> delegate() {
    return events;
  }

  public boolean isConsistsOf(E event) {
    return this.events.equals(singleton(event));
  }

  public boolean hasExactlyOne(Predicate<E> predicate) {
    return this.count(predicate) == 1;
  }

  public long count(Predicate<E> predicate) {
    return this.events.stream().filter(predicate).count();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EventSequence<?> that = (EventSequence<?>) o;
    return Objects.equals(title, that.title) &&
      Objects.equals(duration, that.duration) &&
      Objects.equals(events, that.events);
  }

  @Override
  public int hashCode() {
    int result = title.hashCode();
    result = 31 * result + duration.hashCode();
    result = 31 * result + events.hashCode();
    return result;
  }
}
