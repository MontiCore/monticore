package de.monticore.codegen.cd2java.builder;

import de.monticore.codegen.cd2java.methods.MandatoryMethodGeneratorStrategy;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;

class BuilderMandatoryMethodGeneratorStrategy extends MandatoryMethodGeneratorStrategy {

  private final ASTType builderType;

  BuilderMandatoryMethodGeneratorStrategy(
      final GlobalExtensionManagement glex,
      final ASTType builderType) {
    super(glex);
    this.builderType = builderType;
  }

  @Override
  protected ASTCDMethod createSetter(final ASTCDAttribute ast) {
    ASTCDMethod method = super.createSetter(ast);
    method.setReturnType(this.builderType);
    return method;
  }
}
