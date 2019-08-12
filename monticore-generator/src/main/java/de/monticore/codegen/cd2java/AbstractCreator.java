package de.monticore.codegen.cd2java;

import de.monticore.codegen.cd2java.factories.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

public abstract class AbstractCreator<I, R> extends AbstractDecorator {

  /*
  Used when a new CD object is created in the decorate method
  Cannot be used in CompositeDecorators
  Do not change the input, only create a new output
   */

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
