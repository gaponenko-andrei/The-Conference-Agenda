package agp.util;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Collections {

  public static <T, R> Set<R> mapIntoSet(Collection<T> original, Function<T, R> mapper) {
    return original.stream().map(mapper).collect(toSet());
  }

  public static <T, R> List<R> mapIntoList(List<T> original, Function<T, R> mapper) {
    return original.stream().map(mapper).collect(toList());
  }

  public static <T> List<T> sortIntoList(Collection<T> original, Comparator<T> comparator) {
    return original.stream().sorted(comparator).collect(toList());
  }
}
