package de.monticore.codegen.cd2java.builder;

import de.monticore.codegen.cd2java.factories.CDMethodFactory;
import de.monticore.codegen.cd2java.methods.MandatoryMethodGeneratorStrategy;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;

public class BuilderMandatoryMethodGeneratorStrategy extends MandatoryMethodGeneratorStrategy {

  private final ASTType builderType;

  public BuilderMandatoryMethodGeneratorStrategy(final CDMethodFactory cdMethodFactory, final ASTType builderType) {
    super(cdMethodFactory);
    this.builderType = builderType;
  }

  @Override
  protected ASTCDMethod createSetter(final ASTCDAttribute ast) {
    ASTCDMethod method = super.createSetter(ast);
    method.setReturnType(this.builderType);
    return method;
  }
}
