package de.monticore.codegen.cd2java.builder;

import de.monticore.codegen.cd2java.methods.OptionalMethodGeneratorStrategy;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;

class BuilderOptionalMethodGeneratorStrategy extends OptionalMethodGeneratorStrategy {

  private final ASTType builderType;

  BuilderOptionalMethodGeneratorStrategy(
      final GlobalExtensionManagement glex,
      final ASTType builderType) {
    super(glex);
    this.builderType = builderType;
  }

  @Override
  protected ASTCDMethod createSetMethod(final ASTCDAttribute ast) {
    ASTCDMethod method = super.createSetMethod(ast);
    method.setReturnType(this.builderType);
    return method;
  }

  @Override
  protected ASTCDMethod createSetOptMethod(final ASTCDAttribute ast) {
    ASTCDMethod method = super.createSetOptMethod(ast);
    method.setReturnType(this.builderType);
    return method;
  }

  @Override
  protected ASTCDMethod createSetAbsentMethod(final ASTCDAttribute ast) {
    ASTCDMethod method = super.createSetAbsentMethod(ast);
    method.setReturnType(this.builderType);
    return method;
  }
}
