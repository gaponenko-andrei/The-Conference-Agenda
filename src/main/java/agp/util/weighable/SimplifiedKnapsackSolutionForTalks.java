package agp.util.weighable;

import static agp.util.Collections.mapIntoSet;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import agp.vo.Talk;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.experimental.Delegate;

public final class SimplifiedKnapsackSolutionForTalks implements KnapsackSolutionForTalks {

  private final SimplifiedKnapsackSolution<WeighableTalk, Duration> generalSolution =
    new SimplifiedKnapsackSolution<>();


  @Override
  public Set<Answer> apply(@NonNull Set<Talk> talks, @NonNull Duration goal) {
    return simplifyIntoAnswers(
      generalSolution.apply(
        adaptTalksForGeneralSolution(talks),
        adaptGoalForGeneralSolution(goal)
      )
    );
  }

  /* Methods to adapt (wrap) arguments for contract of general solution */

  private WeighablesCombination<WeighableTalk> adaptTalksForGeneralSolution(Set<Talk> talks) {
    Set<WeighableTalk> weighableTalks = mapIntoSet(talks, WeighableTalk::new);
    return WeighablesCombination.of(weighableTalks);
  }

  private WeighableDuration adaptGoalForGeneralSolution(Duration duration) {
    return new WeighableDuration(duration);
  }

  /* Methods to simplify (unwrap) general generalSolution result into client-known types */

  private Set<Answer> simplifyIntoAnswers(List<WeighablesCombination<WeighableTalk>> combinations) {
    return mapIntoSet(combinations, this::simplifyIntoAnswer);
  }

  private Answer simplifyIntoAnswer(WeighablesCombination<WeighableTalk> combination) {
    Set<Talk> talks = mapIntoSet(combination, WeighableTalk::delegate);
    return new Answer(talks);
  }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
  /*                                Auxiliary Classes                                */
  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

  @Value
  @Accessors(fluent = true)
  private static class WeighableTalk implements Weighable<Duration> {

    @Delegate
    private final Talk delegate;

    @Override
    public Duration weight() {
      return this.duration();
    }

    @Override
    public boolean isPositive() {
      return this.duration().toMinutes() > 0;
    }

    @Override
    public Weighable<Duration> subtract(@NonNull Duration otherDuration) {
      return new WeighableDuration(this.duration().minus(otherDuration));
    }

    @Override
    public String toString() {
      return String.format(
        "WeighableTalk[%s, %s minutes]",
        this.title(), this.duration().toMinutes()
      );
    }
  }

  @Value
  @Accessors(fluent = true)
  private static final class WeighableDuration implements Weighable<Duration> {

    @Delegate
    private final Duration weight;

    @Override
    public boolean isPositive() {
      return this.toMinutes() > 0;
    }

    @Override
    public Weighable<Duration> subtract(Duration otherDuration) {
      return new WeighableDuration(this.minus(otherDuration));
    }

    @Override
    public String toString() {
      return String.format("WeighableDuration[%s minutes]", this.toMinutes());
    }
  }
}
