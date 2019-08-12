package de.monticore.codegen.cd2java;

import de.monticore.codegen.cd2java.factories.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

public abstract class AbstractTransformer<I> extends AbstractDecorator {

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
