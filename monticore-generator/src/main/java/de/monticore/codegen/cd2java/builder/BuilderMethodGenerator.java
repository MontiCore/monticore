package de.monticore.codegen.cd2java.builder;

import de.monticore.codegen.cd2java.factories.CDMethodFactory;
import de.monticore.codegen.cd2java.factories.CDParameterFactory;
import de.monticore.codegen.cd2java.factories.CDTypeFactory;
import de.monticore.codegen.cd2java.methods.*;
import de.monticore.codegen.cd2java.methods.ListMethodGeneratorStrategy;
import de.monticore.codegen.cd2java.methods.MandatoryMethodGeneratorStrategy;
import de.monticore.codegen.cd2java.methods.OptionalMethodGeneratorStrategy;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.se_rwth.commons.logging.Log;

import java.util.List;

public class BuilderMethodGenerator extends MethodGenerator {

  private ASTType builderType;

  public BuilderMethodGenerator(final CDTypeFactory cdTypeFactory, final CDMethodFactory cdMethodFactory, final CDParameterFactory cdParameterFactory) {
    super(cdTypeFactory, cdMethodFactory, cdParameterFactory);
  }

  void setBuilderType(final ASTType builderType) {
    this.builderType = builderType;
  }

  @Override
  public List<ASTCDMethod> generate(ASTCDAttribute ast) {
    if (builderType == null)
      Log.error("The builder type is not set yet, but it is necessary to generate Builder methods.");
    return super.generate(ast);
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
