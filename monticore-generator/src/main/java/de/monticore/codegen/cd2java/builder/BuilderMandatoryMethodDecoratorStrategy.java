package de.monticore.codegen.cd2java.builder;

import de.monticore.codegen.cd2java.methods.MandatoryMethodDecoratorStrategy;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;

class BuilderMandatoryMethodDecoratorStrategy extends MandatoryMethodDecoratorStrategy {

  private final ASTType builderType;

  BuilderMandatoryMethodDecoratorStrategy(
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
