package de.monticore.codegen.cd2java.builder;

import de.monticore.codegen.cd2java.factories.CDMethodFactory;
import de.monticore.codegen.cd2java.methods.ListMethodGeneratorStrategy;
import de.monticore.codegen.cd2java.methods.MandatoryMethodGeneratorStrategy;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;

public class BuilderListMethodGeneratorStrategy extends ListMethodGeneratorStrategy {

  private final ASTType builderType;

  public BuilderListMethodGeneratorStrategy(final CDMethodFactory cdMethodFactory, final MandatoryMethodGeneratorStrategy mandatoryMethodGeneratorStrategy,
      final ASTType builderType) {
    super(cdMethodFactory, mandatoryMethodGeneratorStrategy);
    this.builderType = builderType;
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
