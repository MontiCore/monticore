package de.monticore.codegen.cd2java.methods;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.Decorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;

import java.util.List;

public class MethodDecorator implements Decorator<ASTCDAttribute, List<ASTCDMethod>> {

  private final GlobalExtensionManagement glex;

  public MethodDecorator(
      final GlobalExtensionManagement glex) {
    this.glex = glex;
  }

  protected GlobalExtensionManagement getGlex() {
    return this.glex;
  }

  @Override
  public List<ASTCDMethod> decorate(final ASTCDAttribute ast) {
    MethodDecoratorStrategy methodGeneratorStrategy = determineMethodGeneratorStrategy(ast);
    return methodGeneratorStrategy.decorate(ast);
  }

  private MethodDecoratorStrategy determineMethodGeneratorStrategy(final ASTCDAttribute ast) {
    //TODO: helper durch OO-Ansatz ersetzen (und vereinheitlichen)
    if (GeneratorHelper.isListType(ast.printType())) {
      return createListMethodDecoratorStrategy();
    }
    else if (GeneratorHelper.isOptional(ast)) {
      return createOptionalMethodDecoratorStrategy();
    }
    return createMandatoryMethodDecoratorStrategy();
  }

  protected MandatoryMethodDecoratorStrategy createMandatoryMethodDecoratorStrategy() {
    return new MandatoryMethodDecoratorStrategy(this.getGlex());
  }

  protected OptionalMethodDecoratorStrategy createOptionalMethodDecoratorStrategy() {
    return new OptionalMethodDecoratorStrategy(this.getGlex());
  }

  protected ListMethodDecoratorStrategy createListMethodDecoratorStrategy() {
    return new ListMethodDecoratorStrategy(this.getGlex(), createMandatoryMethodDecoratorStrategy());
  }
}
