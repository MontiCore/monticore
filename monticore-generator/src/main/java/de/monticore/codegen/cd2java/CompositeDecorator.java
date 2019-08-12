package de.monticore.codegen.cd2java;

import de.monticore.generating.templateengine.GlobalExtensionManagement;

import java.util.Arrays;
import java.util.Collection;

public abstract class CompositeDecorator<T> extends AbstractTransformer<T> {

  protected final Collection<AbstractTransformer<T>> decorators;

  @SafeVarargs
  public CompositeDecorator(final AbstractTransformer<T>... decorators) {
    this(Arrays.asList(decorators));
  }

  public CompositeDecorator(final Collection<AbstractTransformer<T>> decorators) {
    this(null, decorators);
  }

  @SafeVarargs
  public CompositeDecorator(final GlobalExtensionManagement glex, final AbstractTransformer<T>... decorators) {
    this(glex, Arrays.asList(decorators));
  }

  public CompositeDecorator(final GlobalExtensionManagement glex, final Collection<AbstractTransformer<T>> decorators) {
    super(glex);
    this.decorators = decorators;
  }

  @Override
  public void enableTemplates() {
    super.enableTemplates();
    this.decorators.forEach(AbstractTransformer::enableTemplates);
  }

  @Override
  public void disableTemplates() {
    super.disableTemplates();
    this.decorators.forEach(AbstractTransformer::disableTemplates);
  }

  @Override
  public T decorate(final T originalInput, T changedInput) {
    return applyDecorations(originalInput, changedInput);
  }

  protected T applyDecorations(final T originalInput, T changedInput) {
    for (AbstractTransformer<T> decorator : decorators) {
      changedInput = decorator.decorate(originalInput, changedInput);
    }
    return changedInput;
  }
}
