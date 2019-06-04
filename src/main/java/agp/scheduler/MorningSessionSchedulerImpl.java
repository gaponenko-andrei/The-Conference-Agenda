package agp.scheduler;

import static com.google.common.base.Preconditions.checkArgument;

import java.time.Duration;
import java.util.Set;

import com.google.common.collect.Sets;

import agp.util.weighable.KnapsackSolutionForTalks;
import agp.util.weighable.KnapsackSolutionForTalks.Answer;
import agp.util.weighable.SimplifiedKnapsackSolutionForTalks;
import agp.vo.MorningSession;
import agp.vo.Talk;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.val;

/* Builder is mostly a replacement for partial application & carrying,
   to avoid 'apply' with several arguments when it's possible & convenient. */

@Accessors(fluent = true)
@Builder(builderClassName = "Builder")
public class MorningSessionSchedulerImpl implements MorningSessionScheduler {

  @Getter @NonNull
  private final Duration sessionGoalDuration;

  @Getter @NonNull
  private final KnapsackSolutionForTalks knapsackSolution;


  @Override
  public Result apply(@NonNull final Set<Talk> availableTalks) {
    requireNonEmpty(availableTalks);

    return findPossibleCombinationsForSessionAmong(availableTalks)
      .stream()
      .findFirst()
      .map(talksCombination -> newResultFrom(availableTalks, talksCombination))
      .orElseThrow(this::newSchedulingException);
  }

  private void requireNonEmpty(Set<Talk> talks) {
    checkArgument(!talks.isEmpty(), "At least one talk is required.");
  }

  private Set<Answer> findPossibleCombinationsForSessionAmong(Set<Talk> availableTalks) {
    return knapsackSolution.apply(availableTalks, sessionGoalDuration);
  }

  private Result newResultFrom(Set<Talk> allTalks, Set<Talk> morningSessionTalks) {
    val unusedTalks = Sets.difference(allTalks, morningSessionTalks);
    return new Result(MorningSession.of(morningSessionTalks), unusedTalks);
  }

  private SchedulingException newSchedulingException() {
    return new SchedulingException(String.format(
      "Failed to schedule morning session of %s " +
        "minutes.", sessionGoalDuration.toMinutes()
    ));
  }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
  /*                             Builder related stuff                               */
  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

  /* Default field values for Lombok-generated Builder */

  public static final class Builder {
    private KnapsackSolutionForTalks knapsackSolution = new SimplifiedKnapsackSolutionForTalks();
  }

  /* Shortcut methods for Builder creation */

  public static MorningSessionSchedulerImpl.Builder using(@NonNull KnapsackSolutionForTalks solution) {
    return builder().knapsackSolution(solution);
  }

  public static MorningSessionSchedulerImpl.Builder using(@NonNull Duration sessionGoalDuration) {
    return builder().sessionGoalDuration(sessionGoalDuration);
  }
}
