/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java;

import de.monticore.cd.facade.CDAttributeFacade;
import de.monticore.cd.facade.CDConstructorFacade;
import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd.facade.CDParameterFacade;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.MCTypeFacade;

public abstract class AbstractCreator<I, R> extends AbstractDecorator {

  /**
  Used when a new CD object is created in the decorate method
  Cannot be used in CompositeDecorators
  Do not change the input, only create a new output
   **/

  public AbstractCreator(final GlobalExtensionManagement glex) {
    super(glex,
        MCTypeFacade.getInstance(),
        CDAttributeFacade.getInstance(),
        CDConstructorFacade.getInstance(),
        CDMethodFacade.getInstance(),
        CDParameterFacade.getInstance(),
        DecorationHelper.getInstance()
    );
  }

  public AbstractCreator() {
    this(null);
  }

  public abstract R decorate(I input);
}
