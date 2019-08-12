package de.monticore.codegen.cd2java;

import de.monticore.codegen.cd2java.factories.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

public abstract class AbstractCreator<I, R> extends AbstractDecorator {

  public AbstractCreator(final GlobalExtensionManagement glex) {
    super(glex,
        CDTypeFacade.getInstance(),
        CDAttributeFacade.getInstance(),
        CDConstructorFacade.getInstance(),
        CDMethodFacade.getInstance(),
        CDParameterFacade.getInstance()
    );
  }

  public AbstractCreator() {
    this(null);
  }

  public abstract R decorate(I input);
}
