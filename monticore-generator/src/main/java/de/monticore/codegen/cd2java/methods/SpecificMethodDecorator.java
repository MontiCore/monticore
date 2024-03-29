/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.methods;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

import java.util.List;

abstract class SpecificMethodDecorator extends AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> {

  protected final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> mandatoryMethodDecorator;

  protected final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> optionalMethodDecorator;

  protected final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> listMethodDecorator;

  SpecificMethodDecorator(final GlobalExtensionManagement glex,
      final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> mandatoryMethodDecorator,
      final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> optionalMethodDecorator,
      final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> listMethodDecorator) {
    super(glex);
    this.mandatoryMethodDecorator = mandatoryMethodDecorator;
    this.optionalMethodDecorator = optionalMethodDecorator;
    this.listMethodDecorator = listMethodDecorator;
  }

  @Override
  public void enableTemplates() {
    mandatoryMethodDecorator.enableTemplates();
    optionalMethodDecorator.enableTemplates();
    listMethodDecorator.enableTemplates();
  }

  @Override
  public void disableTemplates() {
    mandatoryMethodDecorator.disableTemplates();
    optionalMethodDecorator.disableTemplates();
    listMethodDecorator.disableTemplates();
  }

  @Override
  public List<ASTCDMethod> decorate(final ASTCDAttribute ast) {
    AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> specificMethodDecorator = determineMethodDecoratorStrategy(ast);
    return specificMethodDecorator.decorate(ast);
  }

  protected AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> determineMethodDecoratorStrategy(final ASTCDAttribute ast) {
    if (getMCTypeFacade().isBooleanType(ast.getMCType())) {
      return mandatoryMethodDecorator;
    } else if (getDecorationHelper().isListType(ast.printType())) {
      return listMethodDecorator;
    } else if (getDecorationHelper().isOptional(ast.getMCType())) {
      return optionalMethodDecorator;
    }
    return mandatoryMethodDecorator;
  }
}
