package agp.scheduler;

import java.util.Set;
import java.util.function.Function;

import com.google.common.collect.ImmutableSet;

import agp.vo.MorningSession;
import agp.vo.Talk;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;

public interface MorningSessionsScheduler extends
  Function<Set<Talk>, MorningSessionsScheduler.Result> {

  @Value
  @Accessors(fluent = true)
  final class Result {

    @NonNull
    private final ImmutableSet<MorningSession> sessions;

    @NonNull
    private final ImmutableSet<Talk> unusedTalks;


    Result(@NonNull final Set<MorningSession> sessions, @NonNull final Set<Talk> unusedTalks) {
      this.sessions = ImmutableSet.copyOf(sessions);
      this.unusedTalks = ImmutableSet.copyOf(unusedTalks);
    }
  }

}
