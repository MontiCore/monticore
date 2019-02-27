package de.monticore.codegen.cd2java.methods;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.Decorator;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;

import java.util.List;

abstract class SpecificMethodDecorator implements Decorator<ASTCDAttribute, List<ASTCDMethod>> {

  @Override
  public List<ASTCDMethod> decorate(final ASTCDAttribute ast) {
    Decorator<ASTCDAttribute, List<ASTCDMethod>> specificMethodDecorator = determineMethodDecoratorStrategy(ast);
    return specificMethodDecorator.decorate(ast);
  }

  private Decorator<ASTCDAttribute, List<ASTCDMethod>> determineMethodDecoratorStrategy(final ASTCDAttribute ast) {
    //TODO: helper durch OO-Ansatz ersetzen (und vereinheitlichen)
    if (GeneratorHelper.isListType(ast.printType())) {
      return createListMethodDecoratorStrategy();
    }
    else if (GeneratorHelper.isOptional(ast)) {
      return createOptionalMethodDecoratorStrategy();
    }
    return createMandatoryMethodDecoratorStrategy();
  }

  abstract Decorator<ASTCDAttribute, List<ASTCDMethod>> createMandatoryMethodDecoratorStrategy();

  abstract Decorator<ASTCDAttribute, List<ASTCDMethod>> createOptionalMethodDecoratorStrategy();

  abstract Decorator<ASTCDAttribute, List<ASTCDMethod>> createListMethodDecoratorStrategy();
}
