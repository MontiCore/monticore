package de.monticore.codegen.cd2java.builder;

import de.monticore.codegen.cd2java.methods.*;
import de.monticore.codegen.cd2java.methods.ListMethodGeneratorStrategy;
import de.monticore.codegen.cd2java.methods.MandatoryMethodGeneratorStrategy;
import de.monticore.codegen.cd2java.methods.OptionalMethodGeneratorStrategy;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTType;

class BuilderMethodGenerator extends MethodGenerator {

  private final ASTType builderType;

  BuilderMethodGenerator(
      final GlobalExtensionManagement glex,
      final ASTType builderType) {
    super(glex);
    this.builderType = builderType;
  }

  @Override
  protected MandatoryMethodGeneratorStrategy createMandatoryMethodGeneratorStrategy() {
    return new BuilderMandatoryMethodGeneratorStrategy(this.getGlex(), this.builderType);
  }

  @Override
  protected OptionalMethodGeneratorStrategy createOptionalMethodGeneratorStrategy() {
    return new BuilderOptionalMethodGeneratorStrategy(this.getGlex(), this.builderType);
  }

  @Override
  protected ListMethodGeneratorStrategy createListMethodGeneratorStrategy() {
    return new BuilderListMethodGeneratorStrategy(this.getGlex(), this.createMandatoryMethodGeneratorStrategy(), this.builderType);
  }
}
