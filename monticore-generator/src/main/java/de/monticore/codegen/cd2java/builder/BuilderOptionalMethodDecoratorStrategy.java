package de.monticore.codegen.cd2java.builder;

import de.monticore.codegen.cd2java.methods.OptionalMethodDecoratorStrategy;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;

class BuilderOptionalMethodDecoratorStrategy extends OptionalMethodDecoratorStrategy {

  private final ASTType builderType;

  BuilderOptionalMethodDecoratorStrategy(
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
  protected HookPoint createSetImplementation(final ASTCDAttribute ast) {
    return new TemplateHookPoint("builder.opt.Set", ast);
  }

  @Override
  protected ASTCDMethod createSetOptMethod(final ASTCDAttribute ast) {
    ASTCDMethod method = super.createSetOptMethod(ast);
    method.setReturnType(this.builderType);
    return method;
  }

  @Override
  protected HookPoint createSetOptImplementation(final ASTCDAttribute ast) {
    return new TemplateHookPoint("builder.Set", ast);
  }

  @Override
  protected ASTCDMethod createSetAbsentMethod(final ASTCDAttribute ast) {
    ASTCDMethod method = super.createSetAbsentMethod(ast);
    method.setReturnType(this.builderType);
    return method;
  }

  @Override
  protected HookPoint createSetAbsentImplementation(final ASTCDAttribute ast) {
    return new TemplateHookPoint("builder.opt.SetAbsent", ast);
  }
}
