package de.monticore.codegen.cd2java.methods;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;

import java.util.List;

abstract class SpecificMethodDecorator extends AbstractDecorator<ASTCDAttribute, List<ASTCDMethod>> {

  SpecificMethodDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public List<ASTCDMethod> decorate(final ASTCDAttribute ast) {
    AbstractDecorator<ASTCDAttribute, List<ASTCDMethod>> specificMethodDecorator = determineMethodDecoratorStrategy(ast);
    return specificMethodDecorator.decorate(ast);
  }

  private AbstractDecorator<ASTCDAttribute, List<ASTCDMethod>> determineMethodDecoratorStrategy(final ASTCDAttribute ast) {
    //TODO: helper durch OO-Ansatz ersetzen (und vereinheitlichen)
    if (GeneratorHelper.isListType(ast.printType())) {
      return createListMethodDecoratorStrategy();
    }
    else if (GeneratorHelper.isOptional(ast)) {
      return createOptionalMethodDecoratorStrategy();
    }
    return createMandatoryMethodDecoratorStrategy();
  }

  abstract AbstractDecorator<ASTCDAttribute, List<ASTCDMethod>> createMandatoryMethodDecoratorStrategy();

  abstract AbstractDecorator<ASTCDAttribute, List<ASTCDMethod>> createOptionalMethodDecoratorStrategy();

  abstract AbstractDecorator<ASTCDAttribute, List<ASTCDMethod>> createListMethodDecoratorStrategy();
}
