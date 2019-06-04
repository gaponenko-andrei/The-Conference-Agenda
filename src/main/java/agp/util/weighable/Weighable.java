package agp.util.weighable;

import lombok.NonNull;

interface Weighable<C extends Comparable<C>> extends Comparable<Weighable<C>> {

  default boolean isHeavierThan(Weighable<C> other) {
    return this.compareTo(other) > 0;
  }

  default boolean isLighterThan(Weighable<C> other) {
    return this.compareTo(other) < 0;
  }

  default int compareTo(@NonNull Weighable<C> other) {
    return this.weight().compareTo(other.weight());
  }

  C weight();

  boolean isPositive();

  Weighable<C> subtract(C otherWeight);
}
