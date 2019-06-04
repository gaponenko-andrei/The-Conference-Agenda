package agp.util.weighable;

import static agp.util.Collections.mapIntoList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;

public class SimplifiedKnapsackSolutionForInts implements
  BiFunction<List<Integer>, Integer, List<SimplifiedKnapsackSolutionForInts.Answer>> {

  private final SimplifiedKnapsackSolution<WeighableInt, Integer> generalSolution =
    new SimplifiedKnapsackSolution<>();


  @Override
  public List<Answer> apply(@NonNull List<Integer> ints, @NonNull Integer goal) {
    return simplifyIntoAnswers(
      generalSolution.apply(
        adaptIntsForGeneralSolution(ints),
        adaptGoalForGeneralSolution(goal)
      )
    );
  }

  /* Methods to adapt (wrap) arguments for contract of general solution */

  private WeighablesCombination<WeighableInt> adaptIntsForGeneralSolution(List<Integer> ints) {
    List<WeighableInt> weighableInts = mapIntoList(ints, WeighableInt::new);
    return WeighablesCombination.of(weighableInts);
  }

  private WeighableInt adaptGoalForGeneralSolution(Integer integer) {
    return new WeighableInt(integer);
  }

  /* Methods to simplify (unwrap) general generalSolution result into client-known types */

  private List<Answer> simplifyIntoAnswers(List<WeighablesCombination<WeighableInt>> combinations) {
    return mapIntoList(combinations, this::simplifyIntoAnswer);
  }

  private Answer simplifyIntoAnswer(WeighablesCombination<WeighableInt> combination) {
    List<Integer> ints = mapIntoList(combination, WeighableInt::weight);
    return new Answer(ints);
  }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
  /*                                Auxiliary Classes                                */
  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

  public static final class Answer extends ArrayList<Integer> {
    private Answer(@NonNull List<Integer> ints) {
      super(ints);
    }
  }

  @Value
  @Accessors(fluent = true)
  private static final class WeighableInt implements Weighable<Integer> {

    @Getter
    @NonNull
    private final Integer weight;

    @Override
    public boolean isPositive() {
      return weight > 0;
    }

    @Override
    public WeighableInt subtract(@NonNull Integer otherWeight) {
      return new WeighableInt(this.weight - otherWeight);
    }
  }
}
