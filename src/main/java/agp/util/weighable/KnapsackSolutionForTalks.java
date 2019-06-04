package agp.util.weighable;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;

import agp.vo.Talk;
import lombok.NonNull;

public interface KnapsackSolutionForTalks extends
  BiFunction<Set<Talk>, Duration, Set<KnapsackSolutionForTalks.Answer>> {

  final class Answer extends HashSet<Talk> {
    Answer(@NonNull Set<Talk> talks) {
      super(talks);
    }
  }

}
