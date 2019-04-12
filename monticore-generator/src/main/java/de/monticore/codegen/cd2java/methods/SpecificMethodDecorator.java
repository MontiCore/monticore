package de.monticore.codegen.cd2java.methods;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.Decorator;
import de.monticore.codegen.cd2java.factories.CDAttributeFactory;
import de.monticore.codegen.cd2java.factories.CDTypeFactory;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;

import java.util.List;

abstract class SpecificMethodDecorator extends AbstractDecorator<ASTCDAttribute, List<ASTCDMethod>> {

  private final AbstractDecorator<ASTCDAttribute, List<ASTCDMethod>> mandatoryMethodDecorator;

  private final AbstractDecorator<ASTCDAttribute, List<ASTCDMethod>> optionalMethodDecorator;

  private final AbstractDecorator<ASTCDAttribute, List<ASTCDMethod>> listMethodDecorator;

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
    if(getCDTypeFactory().isBooleanType(ast.getType())){
      return mandatoryMethodDecorator;
    }
    //TODO: helper durch OO-Ansatz ersetzen (und vereinheitlichen)
    else if (GeneratorHelper.isListType(ast.printType())) {
      return listMethodDecorator;
    }
    else if (GeneratorHelper.isOptional(ast)) {
      return optionalMethodDecorator;
    }
    return mandatoryMethodDecorator;
  }
}
