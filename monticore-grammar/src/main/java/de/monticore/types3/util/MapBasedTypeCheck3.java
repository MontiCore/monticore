package de.monticore.types3.util;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.ASTMCVoidType;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.TypeCheck3;
import de.monticore.types3.generics.context.InferenceContext4Ast;
import de.monticore.visitor.ITraverser;
import de.se_rwth.commons.logging.Log;

/**
 * A default implementation of the TypeCheck3 interface.
 * This class is designed to be derived from,
 * such that the required traverser and maps are provided by the subclasses.
 * <p>
 * This implementation is based on the Maps
 * {@link Type4Ast} and {@link InferenceContext4Ast},
 * which filled using a traverser.
 * {@link Type4Ast} is filled with the result of the type derivation,
 * while {@link InferenceContext4Ast} is used to provide information
 * to the traverser, e.g., an expressions target type.
 * <p>
 * If the type has already been derived and stored in {@link Type4Ast},
 * then it will not be derived again.
 */
public class MapBasedTypeCheck3 extends TypeCheck3 {

  protected ITraverser typeTraverser;

  protected Type4Ast type4Ast;

  protected InferenceContext4Ast ctx4Ast;

  /**
   * S.a. {@link #setThisAsDelegate()}.
   *
   * @param typeTraverser traverser filling type4Ast, language specific
   * @param type4Ast      a map of types to be filled
   * @param ctx4Ast       a map of contexts to be filled
   */
  public MapBasedTypeCheck3(
      ITraverser typeTraverser,
      Type4Ast type4Ast,
      InferenceContext4Ast ctx4Ast
  ) {
    this.typeTraverser = Log.errorIfNull(typeTraverser);
    this.type4Ast = Log.errorIfNull(type4Ast);
    this.ctx4Ast = Log.errorIfNull(ctx4Ast);
  }

  /**
   * This will be set as the TypeCheck3 delegate.
   */
  public void setThisAsDelegate() {
    TypeCheck3.setDelegate(this);
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

  //-----------TypeCheck3 static delegate implementation----------//

  @Override
  public SymTypeExpression _symTypeFromAST(ASTMCType mcType) {
    if (!getType4Ast().hasTypeOfTypeIdentifier(mcType)) {
      mcType.accept(getTypeTraverser());
    }
    return getType4Ast().getPartialTypeOfTypeId(mcType);
  }

  @Override
  public SymTypeExpression _symTypeFromAST(ASTMCVoidType mcVoidType) {
    return SymTypeExpressionFactory.createTypeVoid();
  }

  @Override
  public SymTypeExpression _symTypeFromAST(ASTMCReturnType mcReturnType) {
    if (!getType4Ast().hasTypeOfTypeIdentifier(mcReturnType)) {
      mcReturnType.accept(getTypeTraverser());
    }
    return getType4Ast().getPartialTypeOfTypeId(mcReturnType);
  }

  @Override
  public SymTypeExpression _symTypeFromAST(ASTMCQualifiedName mcQualifiedName) {
    if (!getType4Ast().hasTypeOfTypeIdentifier(mcQualifiedName)) {
      mcQualifiedName.accept(getTypeTraverser());
    }
    return getType4Ast().getPartialTypeOfTypeId(mcQualifiedName);
  }

  @Override
  public SymTypeExpression _typeOf(ASTExpression expr) {
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
  public SymTypeExpression _typeOf(
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
  public SymTypeExpression _typeOf(ASTLiteral lit) {
    if (!getType4Ast().hasTypeOfExpression(lit)) {
      lit.accept(getTypeTraverser());
    }
    return getType4Ast().getPartialTypeOfExpr(lit);
  }

}
