package de.monticore.codegen.cd2java;

import de.monticore.codegen.cd2java.factories.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

public abstract class AbstractTransformer<I> extends AbstractDecorator {

  /*
  Use when Decorator has to change another object
  Has to be used when Decorator is part if a CompositeDecorator
  Do not change the originalInput, but change the ChangedInput
   */

  public AbstractTransformer(final GlobalExtensionManagement glex) {
    super(glex,
        CDTypeFacade.getInstance(),
        CDAttributeFacade.getInstance(),
        CDConstructorFacade.getInstance(),
        CDMethodFacade.getInstance(),
        CDParameterFacade.getInstance()
    );
  }

  public AbstractTransformer() {
    this(null);
  }

  public abstract I decorate(final I originalInput, I changedInput);
}
