package agp.vo;

import java.util.Set;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class AfternoonSession extends EventSequence<Talk> {

  public static AfternoonSession of(@NonNull final Set<Talk> events) {
    return new AfternoonSession(events);
  }

  private AfternoonSession(@NonNull final Set<Talk> events) {
    super("Afternoon session", events);
  }
}
