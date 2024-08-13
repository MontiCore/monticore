package de.monticore.types3;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.ASTMCVoidType;
import de.monticore.types3.generics.context.InferenceContext4Ast;
import de.monticore.visitor.ITraverser;
import de.se_rwth.commons.logging.Log;

/**
 * Temporary(!) interface implementation for the temporary(!) usage of TC3.
 * This is temporary as the interface had not been discussed yet.
 * s. https://git.rwth-aachen.de/monticore/monticore/-/issues/3420
 * @deprecated use {@link TypeCheck3}
 */
@Deprecated
public class TypeCalculator3 implements ITypeCalculator {

  protected ITraverser typeTraverser;

  protected Type4Ast type4Ast;

  protected InferenceContext4Ast ctx4Ast;

  /**
   * @param typeTraverser traverser filling type4Ast, language specific
   * @param type4Ast      a map of types to be filled
   * @param ctx4Ast       a map of contexts to be filled
   */
  public TypeCalculator3(
      ITraverser typeTraverser,
      Type4Ast type4Ast,
      InferenceContext4Ast ctx4Ast
  ) {
    this.typeTraverser = Log.errorIfNull(typeTraverser);
    this.type4Ast = Log.errorIfNull(type4Ast);
    this.ctx4Ast = Log.errorIfNull(ctx4Ast);
  }

  public ITraverser getTypeTraverser() {
    return typeTraverser;
  }

  public Type4Ast getType4Ast() {
    return type4Ast;
  }

  public InferenceContext4Ast getCtx4Ast() {
    return ctx4Ast;
  }

  @Override
  public SymTypeExpression symTypeFromAST(ASTMCType mcType) {
    if (!getType4Ast().hasTypeOfTypeIdentifier(mcType)) {
      mcType.accept(getTypeTraverser());
    }
    return getType4Ast().getPartialTypeOfTypeId(mcType);
  }

  @Override
  public SymTypeExpression symTypeFromAST(ASTMCVoidType mcVoidType) {
    return SymTypeExpressionFactory.createTypeVoid();
  }

  @Override
  public SymTypeExpression symTypeFromAST(ASTMCReturnType mcReturnType) {
    if (!getType4Ast().hasTypeOfTypeIdentifier(mcReturnType)) {
      mcReturnType.accept(getTypeTraverser());
    }
    return getType4Ast().getPartialTypeOfTypeId(mcReturnType);
  }

  @Override
  public SymTypeExpression symTypeFromAST(ASTMCQualifiedName mcQualifiedName) {
    if (!getType4Ast().hasTypeOfTypeIdentifier(mcQualifiedName)) {
      mcQualifiedName.accept(getTypeTraverser());
    }
    return getType4Ast().getPartialTypeOfTypeId(mcQualifiedName);
  }

  @Override
  public SymTypeExpression typeOf(
      ASTExpression expr,
      SymTypeExpression targetType
  ) {
    // need to reset in case the target type changes the typing in the AST
    getType4Ast().reset(expr);
    getCtx4Ast().reset(expr);
    getCtx4Ast().setTargetTypeOfExpression(expr, targetType);
    expr.accept(getTypeTraverser());
    return getType4Ast().getPartialTypeOfExpr(expr);
  }

  @Override
  public SymTypeExpression typeOf(ASTExpression expr) {
    // reset, if target typing had been used, as currently,
    // there is no target type
    if (getCtx4Ast().getContextOfExpression(expr).hasTargetType()) {
      getType4Ast().reset(expr);
      getCtx4Ast().reset(expr);
    }
    if (!getType4Ast().hasTypeOfExpression(expr)) {
      expr.accept(getTypeTraverser());
    }
    return getType4Ast().getPartialTypeOfExpr(expr);
  }

  @Override
  public SymTypeExpression typeOf(ASTLiteral lit) {
    if (!getType4Ast().hasTypeOfExpression(lit)) {
      lit.accept(getTypeTraverser());
    }
    return getType4Ast().getPartialTypeOfExpr(lit);
  }

}
