package agp.util;

import java.util.List;

import com.google.common.collect.ForwardingList;
import com.google.common.collect.ImmutableList;

import lombok.NonNull;
import lombok.val;

public final class HeadTailImmutableList<E> extends ForwardingList<E> {

  private final ImmutableList<E> delegate;


  public static <E> HeadTailImmutableList<E> of(@NonNull List<E> elements) {
    val original = ImmutableList.copyOf(elements);
    return new HeadTailImmutableList<>(original);
  }

  private HeadTailImmutableList(ImmutableList<E> original) {
    this.delegate = original;
  }

  public E head() {
    return delegate.get(0);
  }

  public HeadTailImmutableList<E> tail() {
    val tailElements = delegate.subList(1, delegate.size());
    return HeadTailImmutableList.of(tailElements);
  }

  @Override
  protected List<E> delegate() {
    return delegate;
  }
}
