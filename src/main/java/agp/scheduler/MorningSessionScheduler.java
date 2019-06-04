package agp.scheduler;

import java.util.Set;
import java.util.function.Function;

import com.google.common.collect.ImmutableSet;

import agp.vo.MorningSession;
import agp.vo.Talk;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;

public interface MorningSessionScheduler extends
  Function<Set<Talk>, MorningSessionScheduler.Result> {

  @Value
  @Accessors(fluent = true)
  final class Result {

    @NonNull
    private final MorningSession session;

    @NonNull
    private final ImmutableSet<Talk> unusedTalks;


    public Result(@NonNull final MorningSession session, @NonNull final Set<Talk> unusedTalks) {
      this.session = session;
      this.unusedTalks = ImmutableSet.copyOf(unusedTalks);
    }
  }
}
