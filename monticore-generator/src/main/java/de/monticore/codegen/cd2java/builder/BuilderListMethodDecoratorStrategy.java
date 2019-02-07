package de.monticore.codegen.cd2java.builder;

import de.monticore.codegen.cd2java.methods.ListMethodDecoratorStrategy;
import de.monticore.codegen.cd2java.methods.MandatoryMethodDecoratorStrategy;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;

class BuilderListMethodDecoratorStrategy extends ListMethodDecoratorStrategy {

  private final ASTType builderType;

  BuilderListMethodDecoratorStrategy(
      final GlobalExtensionManagement glex,
      final MandatoryMethodDecoratorStrategy mandatoryMethodDecoratorStrategy,
      final ASTType builderType) {
    super(glex, mandatoryMethodDecoratorStrategy);
    this.builderType = builderType;
  }

  @Override
  protected HookPoint createImplementation(String attributeName, String methodName, String parameterCall, String returnType) {
    if (!TypesPrinter.printType(builderType).equals(returnType)) {
      return super.createImplementation(attributeName, methodName, parameterCall, returnType);
    }
    return new TemplateHookPoint("builder.MethodDelegate", attributeName, methodName, parameterCall, returnType);
  }

  @Override
  protected ASTCDMethod createClearMethod() {
    ASTCDMethod method = super.createClearMethod();
    method.setReturnType(this.builderType);
    return method;
  }

  @Override
  protected ASTCDMethod createAddMethod() {
    ASTCDMethod method = super.createAddMethod();
    method.setReturnType(this.builderType);
    return method;
  }

  @Override
  protected ASTCDMethod createAddAllMethod() {
    ASTCDMethod method = super.createAddAllMethod();
    method.setReturnType(this.builderType);
    return method;
  }

  @Override
  protected ASTCDMethod createRemoveMethod() {
    ASTCDMethod method = super.createRemoveMethod();
    method.setReturnType(this.builderType);
    return method;
  }

  @Override
  protected ASTCDMethod createRemoveAllMethod() {
    ASTCDMethod method = super.createRemoveAllMethod();
    method.setReturnType(this.builderType);
    return method;
  }

  @Override
  protected ASTCDMethod createRetainAllMethod() {
    ASTCDMethod method = super.createRetainAllMethod();
    method.setReturnType(this.builderType);
    return method;
  }

  @Override
  protected ASTCDMethod createRemoveIfMethod() {
    ASTCDMethod method = super.createRemoveIfMethod();
    method.setReturnType(this.builderType);
    return method;
  }

  @Override
  protected ASTCDMethod createForEachMethod() {
    ASTCDMethod method = super.createForEachMethod();
    method.setReturnType(this.builderType);
    return method;
  }

  @Override
  protected ASTCDMethod createAdd_Method() {
    ASTCDMethod method = super.createAdd_Method();
    method.setReturnType(this.builderType);
    return method;
  }

  @Override
  protected ASTCDMethod createAddAll_Method() {
    ASTCDMethod method = super.createAddAll_Method();
    method.setReturnType(this.builderType);
    return method;
  }

  @Override
  protected ASTCDMethod createRemove_Method() {
    ASTCDMethod method = super.createRemove_Method();
    method.setReturnType(this.builderType);
    return method;
  }

  @Override
  protected ASTCDMethod createSetMethod() {
    ASTCDMethod method = super.createSetMethod();
    method.setReturnType(this.builderType);
    return method;
  }

  @Override
  protected ASTCDMethod createReplaceAllMethod() {
    ASTCDMethod method = super.createReplaceAllMethod();
    method.setReturnType(this.builderType);
    return method;
  }

  @Override
  protected ASTCDMethod createSortMethod() {
    ASTCDMethod method = super.createSortMethod();
    method.setReturnType(this.builderType);
    return method;
  }
}
