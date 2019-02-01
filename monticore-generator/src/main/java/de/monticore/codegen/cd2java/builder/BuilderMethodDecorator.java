package de.monticore.codegen.cd2java.builder;

import de.monticore.codegen.cd2java.methods.*;
import de.monticore.codegen.cd2java.methods.ListMethodDecoratorStrategy;
import de.monticore.codegen.cd2java.methods.MandatoryMethodDecoratorStrategy;
import de.monticore.codegen.cd2java.methods.OptionalMethodDecoratorStrategy;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTType;

class BuilderMethodDecorator extends MethodDecorator {

  private final ASTType builderType;

  BuilderMethodDecorator(
      final GlobalExtensionManagement glex,
      final ASTType builderType) {
    super(glex);
    this.builderType = builderType;
  }

  @Override
  protected MandatoryMethodDecoratorStrategy createMandatoryMethodDecoratorStrategy() {
    return new BuilderMandatoryMethodDecoratorStrategy(this.getGlex(), this.builderType);
  }

  @Override
  protected OptionalMethodDecoratorStrategy createOptionalMethodDecoratorStrategy() {
    return new BuilderOptionalMethodDecoratorStrategy(this.getGlex(), this.builderType);
  }

  @Override
  protected ListMethodDecoratorStrategy createListMethodDecoratorStrategy() {
    return new BuilderListMethodDecoratorStrategy(this.getGlex(), this.createMandatoryMethodDecoratorStrategy(), this.builderType);
  }
}
