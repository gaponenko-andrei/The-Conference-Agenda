package agp.util.weighable;

import static java.util.Collections.singletonList;

import java.util.List;

import com.google.common.collect.ForwardingList;
import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;

@Value
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
final class WeighablesCombination<W extends Weighable> extends ForwardingList<W> {

  @NonNull
  private final ImmutableList<W> elements;


  public static <W extends Weighable> WeighablesCombination<W> of(@NonNull Iterable<W> elements) {
    return new WeighablesCombination<>(elements);
  }

  public static <W extends Weighable> WeighablesCombination<W> of(@NonNull W element) {
    return new WeighablesCombination<>(singletonList(element));
  }

  private WeighablesCombination(Iterable<W> elements) {
    this.elements = ImmutableList.copyOf(elements);
  }

  public WeighablesCombination<W> union(WeighablesCombination<W> that) {
    return new WeighablesCombination<>(
      ImmutableList
        .<W>builder()
        .addAll(this.elements)
        .addAll(that.elements)
        .build()
    );
  }

  @Override
  public String toString() {
    return "WeighablesCombination.of(" + elements + ')';
  }

  @Override
  protected List<W> delegate() {
    return elements;
  }
}
