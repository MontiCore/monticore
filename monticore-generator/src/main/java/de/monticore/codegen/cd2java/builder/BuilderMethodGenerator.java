package de.monticore.codegen.cd2java.builder;

import de.monticore.codegen.cd2java.factories.CDMethodFactory;
import de.monticore.codegen.cd2java.factories.CDParameterFactory;
import de.monticore.codegen.cd2java.factories.CDTypeFactory;
import de.monticore.codegen.cd2java.methods.ListMethodGeneratorStrategy;
import de.monticore.codegen.cd2java.methods.MandatoryMethodGeneratorStrategy;
import de.monticore.codegen.cd2java.methods.MethodGenerator;
import de.monticore.codegen.cd2java.methods.OptionalMethodGeneratorStrategy;
import de.monticore.types.types._ast.ASTType;

public class BuilderMethodGenerator extends MethodGenerator {

  private final ASTType builderType;

  public BuilderMethodGenerator(final ASTType builderType) {
    this.builderType = builderType;
  }

  protected MandatoryMethodGeneratorStrategy createMandatoryMethodGeneratorStrategy(final CDMethodFactory cdMethodFactory) {
    return new BuilderMandatoryMethodGeneratorStrategy(cdMethodFactory, this.builderType);
  }

  protected OptionalMethodGeneratorStrategy createOptionalMethodGeneratorStrategy(final CDTypeFactory cdTypeFactory, final CDMethodFactory cdMethodFactory,
      final CDParameterFactory cdParameterFactory) {
    return new BuilderOptionalMethodGeneratorStrategy(cdTypeFactory, cdMethodFactory, cdParameterFactory, this.builderType);
  }

  protected ListMethodGeneratorStrategy createListMethodGeneratorStrategy(final CDMethodFactory cdMethodFactory) {
    return new BuilderListMethodGeneratorStrategy(cdMethodFactory, new MandatoryMethodGeneratorStrategy(cdMethodFactory), this.builderType);
  }
}
