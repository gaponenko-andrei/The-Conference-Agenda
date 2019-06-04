package agp.vo;

import java.util.Set;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class MorningSession extends EventSequence<Talk> {

  public static MorningSession of(@NonNull final Set<Talk> events) {
    return new MorningSession(events);
  }

  private MorningSession(@NonNull final Set<Talk> events) {
    super("Morning session", events);
  }
}
