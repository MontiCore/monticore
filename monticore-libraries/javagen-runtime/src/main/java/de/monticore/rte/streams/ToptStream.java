/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rte.streams;

import java.util.Optional;

public class ToptStream<T> extends SyncStream<Optional<T>> {

  ToptStream(UntimedStream<Optional<T>> backing) {
    super(backing);
  }

  @SafeVarargs
  public static <S> ToptStream<S> of(Optional<S>... elem) {
    return new ToptStream<>(UntimedStream.of(elem));
  }

  @Override
  public int hashCode() {
    return super.hashCode() ^ 3;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof ToptStream))
      return false;

    ToptStream<?> other = (ToptStream<?>) obj;
    return this.getBacking().equals(other.getBacking());
  }
}
