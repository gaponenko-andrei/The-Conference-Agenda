package agp.util.weighable;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Iterables.all;
import static java.util.Collections.*;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.function.BiFunction;

import com.google.common.collect.ImmutableList;

import agp.util.Collections;
import agp.util.HeadTailImmutableList;
import lombok.NonNull;
import lombok.val;

final class SimplifiedKnapsackSolution<W extends Weighable<C>, C extends Comparable<C>>
  implements BiFunction<WeighablesCombination<W>, Weighable<C>, List<WeighablesCombination<W>>> {

  @Override
  public List<WeighablesCombination<W>> apply(
    @NonNull final WeighablesCombination<W> weighables,
    @NonNull final Weighable<C> goal) {

    checkArgument(goal.isPositive(), "Goal must be positive.");
    checkArgument(!weighables.isEmpty(), "At least one weighable is required.");
    checkArgument(all(weighables, Weighable::isPositive), "All weighables must be positive.");

    val descSortedWeighables = Collections.sortIntoList(weighables, reverseOrder());
    return allCombinationsFor(HeadTailImmutableList.of(descSortedWeighables), goal);
  }

  private List<WeighablesCombination<W>> allCombinationsFor(
    final HeadTailImmutableList<W> weighables, final Weighable<C> goal) {

    val allCombinations = ImmutableList.<WeighablesCombination<W>>builder();

    for (HeadTailImmutableList<W> unprocessedWeighables = weighables;
         !unprocessedWeighables.isEmpty();
         unprocessedWeighables = unprocessedWeighables.tail()) {

      allCombinations.addAll(combinationsStartingWithHeadFor(unprocessedWeighables, goal));
    }

    return allCombinations.build();
  }

  private List<WeighablesCombination<W>> combinationsStartingWithHeadFor(
    final HeadTailImmutableList<W> weighables, final Weighable<C> goal) {

    val headElement = weighables.head();

    if (weighables.isEmpty() || headElement.isHeavierThan(goal))
      return emptyList();

    else if (headElement.isLighterThan(goal))
      return headAndTailUnionCombinationsFor(weighables, goal);

    return listWithOneCombinationOf(headElement);
  }

  private List<WeighablesCombination<W>> listWithOneCombinationOf(W weighable) {
    return singletonList(WeighablesCombination.of(weighable));
  }

  private List<WeighablesCombination<W>> headAndTailUnionCombinationsFor(
    final HeadTailImmutableList<W> weighables, final Weighable<C> goal) {

    val thisHeadCombination = WeighablesCombination.of(weighables.head());
    val desiredTailWeight = goal.subtract(weighables.head().weight());
    val tailCombinations = allCombinationsFor(weighables.tail(), desiredTailWeight);
    return tailCombinations.stream().map(thisHeadCombination::union).collect(toList());
  }
}
