package de.monticore.codegen.cd2java.methods;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.Decorator;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

import java.util.List;

abstract class SpecificMethodDecorator extends AbstractDecorator<ASTCDAttribute, List<ASTCDMethod>> {

  protected final AbstractDecorator<ASTCDAttribute, List<ASTCDMethod>> mandatoryMethodDecorator;

  protected final AbstractDecorator<ASTCDAttribute, List<ASTCDMethod>> optionalMethodDecorator;

  protected final AbstractDecorator<ASTCDAttribute, List<ASTCDMethod>> listMethodDecorator;

  SpecificMethodDecorator(final GlobalExtensionManagement glex,
      final AbstractDecorator<ASTCDAttribute, List<ASTCDMethod>> mandatoryMethodDecorator,
      final AbstractDecorator<ASTCDAttribute, List<ASTCDMethod>> optionalMethodDecorator,
      final AbstractDecorator<ASTCDAttribute, List<ASTCDMethod>> listMethodDecorator) {
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
    Decorator<ASTCDAttribute, List<ASTCDMethod>> specificMethodDecorator = determineMethodDecoratorStrategy(ast);
    return specificMethodDecorator.decorate(ast);
  }

  private Decorator<ASTCDAttribute, List<ASTCDMethod>> determineMethodDecoratorStrategy(final ASTCDAttribute ast) {
    if(getCDTypeFacade().isBooleanType(ast.getMCType())){
      return mandatoryMethodDecorator;
    }
    //TODO: helper durch OO-Ansatz ersetzen (und vereinheitlichen)
    else if (DecorationHelper.isListType(ast.printType())) {
      return listMethodDecorator;
    }
    else if (DecorationHelper.isOptional(ast.getMCType())) {
      return optionalMethodDecorator;
    }
    return mandatoryMethodDecorator;
  }
}
