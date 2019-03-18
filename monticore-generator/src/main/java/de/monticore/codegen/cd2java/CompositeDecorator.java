package de.monticore.codegen.cd2java;

import de.monticore.generating.templateengine.GlobalExtensionManagement;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class CompositeDecorator<T> extends AbstractDecorator<T, T> {

  private final Collection<AbstractDecorator<T, T>> decorators;

  @SafeVarargs
  public CompositeDecorator(final AbstractDecorator<T, T>... decorators) {
    this(Arrays.asList(decorators));
  }

  public CompositeDecorator(final Collection<AbstractDecorator<T, T>> decorators) {
    this(null, decorators);
  }

  @SafeVarargs
  public CompositeDecorator(final GlobalExtensionManagement glex, final AbstractDecorator<T, T>... decorators) {
    this(glex, Arrays.asList(decorators));
  }

  public CompositeDecorator(final GlobalExtensionManagement glex, final Collection<AbstractDecorator<T, T>> decorators) {
    super(glex);
    this.decorators = decorators;
  }

  @Override
  public void enableTemplates() {
    super.enableTemplates();
    this.decorators.forEach(AbstractDecorator::enableTemplates);
  }

  @Override
  public void disableTemplates() {
    super.disableTemplates();
    this.decorators.forEach(AbstractDecorator::disableTemplates);
  }

  @Override
  public T decorate(final T input) {
    Stream<T> stream = Stream.of(input);
    for (AbstractDecorator<T, T> decorator : decorators) {
      stream = stream.map(decorator::decorate);
    }
    return stream.collect(toSingleton());
  }

  private static <T> Collector<T, ?, T> toSingleton() {
    return Collectors.collectingAndThen(
        Collectors.toList(),
        list -> {
          if (list.size() != 1) {
            throw new IllegalStateException();
          }
          return list.get(0);
        }
    );
  }
}
