package de.monticore.codegen.cd2java;

import de.monticore.ast.ASTNode;
import de.monticore.codegen.cd2java.factories.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;

public abstract class AbstractDecorator<I, R> implements Decorator<I, R> {

  private final GlobalExtensionManagement glex;

  private boolean attachTemplates;

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
    this.attachTemplates = true;
    this.cdTypeFactory = cdTypeFactory;
    this.cdAttributeFactory = cdAttributeFactory;
    this.cdConstructorFactory = cdConstructorFactory;
    this.cdMethodFactory = cdMethodFactory;
    this.cdParameterFactory = cdParameterFactory;
  }

  public boolean enableTemplates() {
    return this.attachTemplates = true;
  }

  public boolean disableTemplates() {
    return this.attachTemplates = false;
  }

  protected void replaceTemplate(String template, ASTNode node, HookPoint hookPoint) {
    if (this.attachTemplates) {
      this.glex.replaceTemplate(template, node, hookPoint);
    }
  }

  protected GlobalExtensionManagement getGlex() {
    return this.glex;
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
