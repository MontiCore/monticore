/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java;

import de.monticore.cd.facade.CDAttributeFacade;
import de.monticore.cd.facade.CDConstructorFacade;
import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd.facade.CDParameterFacade;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.MCTypeFacade;

public abstract class AbstractTransformer<I> extends AbstractDecorator {

  /**
  Use when Decorator has to change another object
  Has to be used when Decorator is part if a CompositeDecorator
  Do not change the originalInput, but change the ChangedInput
   **/

  public AbstractTransformer(final GlobalExtensionManagement glex) {
    super(glex,
        MCTypeFacade.getInstance(),
        CDAttributeFacade.getInstance(),
        CDConstructorFacade.getInstance(),
        CDMethodFacade.getInstance(),
        CDParameterFacade.getInstance(),
        DecorationHelper.getInstance()
    );
  }

  public AbstractTransformer() {
    this(null);
  }

  public abstract I decorate(final I originalInput, I changedInput);
}
