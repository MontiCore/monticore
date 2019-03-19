package de.monticore.codegen.cd2java;

import de.monticore.ast.ASTNode;
import de.monticore.codegen.cd2java.factories.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;

public abstract class AbstractDecorator<I, R> implements Decorator<I, R> {

  private final GlobalExtensionManagement glex;

  private boolean templatesEnabled;

  private final CDTypeFactory cdTypeFactory;

  private final CDAttributeFactory cdAttributeFactory;

  private final CDConstructorFactory cdConstructorFactory;

  private final CDMethodFactory cdMethodFactory;

  private final CDParameterFactory cdParameterFactory;

  public AbstractDecorator() {
    this(null);
  }

  public AbstractDecorator(final GlobalExtensionManagement glex) {
    this(glex,
        CDTypeFactory.getInstance(),
        CDAttributeFactory.getInstance(),
        CDConstructorFactory.getInstance(),
        CDMethodFactory.getInstance(),
        CDParameterFactory.getInstance()
    );
  }

  public AbstractDecorator(final GlobalExtensionManagement glex,
      final CDTypeFactory cdTypeFactory,
      final CDAttributeFactory cdAttributeFactory,
      final CDConstructorFactory cdConstructorFactory,
      final CDMethodFactory cdMethodFactory,
      final CDParameterFactory cdParameterFactory) {
    this.glex = glex;
    this.templatesEnabled = true;
    this.cdTypeFactory = cdTypeFactory;
    this.cdAttributeFactory = cdAttributeFactory;
    this.cdConstructorFactory = cdConstructorFactory;
    this.cdMethodFactory = cdMethodFactory;
    this.cdParameterFactory = cdParameterFactory;
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

  protected CDTypeFactory getCDTypeFactory() {
    return this.cdTypeFactory;
  }

  protected CDAttributeFactory getCDAttributeFactory() {
    return this.cdAttributeFactory;
  }

  protected CDConstructorFactory getCDConstructorFactory() {
    return this.cdConstructorFactory;
  }

  protected CDMethodFactory getCDMethodFactory() {
    return this.cdMethodFactory;
  }

  protected CDParameterFactory getCDParameterFactory() {
    return this.cdParameterFactory;
  }
}
