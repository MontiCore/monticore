package de.monticore.codegen.cd2java.builder;

import de.monticore.codegen.cd2java.factories.CDMethodFactory;
import de.monticore.codegen.cd2java.factories.CDParameterFactory;
import de.monticore.codegen.cd2java.factories.CDTypeFactory;
import de.monticore.codegen.cd2java.methods.*;
import de.monticore.codegen.cd2java.methods.ListMethodGeneratorStrategy;
import de.monticore.codegen.cd2java.methods.MandatoryMethodGeneratorStrategy;
import de.monticore.codegen.cd2java.methods.OptionalMethodGeneratorStrategy;
import de.monticore.types.types._ast.ASTType;

class BuilderMethodGenerator extends MethodGenerator {

  private final ASTType builderType;

  BuilderMethodGenerator(final CDTypeFactory cdTypeFactory, final CDMethodFactory cdMethodFactory, final CDParameterFactory cdParameterFactory,
      final ASTType builderType) {
    super(cdTypeFactory, cdMethodFactory, cdParameterFactory);
    this.builderType = builderType;
  }

  @Override
  protected MandatoryMethodGeneratorStrategy createMandatoryMethodGeneratorStrategy() {
    return new BuilderMandatoryMethodGeneratorStrategy(this.getCDMethodFactory(), this.builderType);
  }

  @Override
  protected OptionalMethodGeneratorStrategy createOptionalMethodGeneratorStrategy() {
    return new BuilderOptionalMethodGeneratorStrategy(this.getCDTypeFactory(), this.getCDMethodFactory(), this.getCDParameterFactory(), this.builderType);
  }

  @Override
  protected ListMethodGeneratorStrategy createListMethodGeneratorStrategy() {
    return new BuilderListMethodGeneratorStrategy(this.getCDMethodFactory(), this.createMandatoryMethodGeneratorStrategy(), this.builderType);
  }
}
