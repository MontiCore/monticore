package de.monticore.codegen.cd2java;

import de.monticore.ast.ASTNode;
import de.monticore.codegen.cd2java.factories.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;

public abstract class AbstractDecorator<I, R> implements Decorator<I, R> {

  protected final GlobalExtensionManagement glex;

  private boolean templatesEnabled;

  private final CDTypeFacade cdTypeFacade;

  private final CDAttributeFacade cdAttributeFacade;

  private final CDConstructorFacade cdConstructorFacade;

  private final CDMethodFacade cdMethodFacade;

  private final CDParameterFacade cdParameterFacade;

  public AbstractDecorator() {
    this(null);
  }

  public AbstractDecorator(final GlobalExtensionManagement glex) {
    this(glex,
        CDTypeFacade.getInstance(),
        CDAttributeFacade.getInstance(),
        CDConstructorFacade.getInstance(),
        CDMethodFacade.getInstance(),
        CDParameterFacade.getInstance()
    );
  }

  public AbstractDecorator(final GlobalExtensionManagement glex,
      final CDTypeFacade cdTypeFacade,
      final CDAttributeFacade cdAttributeFacade,
      final CDConstructorFacade cdConstructorFacade,
      final CDMethodFacade cdMethodFacade,
      final CDParameterFacade cdParameterFacade) {
    this.glex = glex;
    this.templatesEnabled = true;
    this.cdTypeFacade = cdTypeFacade;
    this.cdAttributeFacade = cdAttributeFacade;
    this.cdConstructorFacade = cdConstructorFacade;
    this.cdMethodFacade = cdMethodFacade;
    this.cdParameterFacade = cdParameterFacade;
  }

  public void enableTemplates() {
    this.templatesEnabled = true;
  }

  public void disableTemplates() {
    this.templatesEnabled = false;
  }

  private boolean templatesEnabled() {
    return this.templatesEnabled;
  }

  protected void replaceTemplate(String template, ASTNode node, HookPoint hookPoint) {
    if (this.templatesEnabled()) {
      this.glex.replaceTemplate(template, node, hookPoint);
    }
  }

  protected CDTypeFacade getCDTypeFacade() {
    return this.cdTypeFacade;
  }

  protected CDAttributeFacade getCDAttributeFacade() {
    return this.cdAttributeFacade;
  }

  protected CDConstructorFacade getCDConstructorFacade() {
    return this.cdConstructorFacade;
  }

  protected CDMethodFacade getCDMethodFacade() {
    return this.cdMethodFacade;
  }

  protected CDParameterFacade getCDParameterFacade() {
    return this.cdParameterFacade;
  }
}
