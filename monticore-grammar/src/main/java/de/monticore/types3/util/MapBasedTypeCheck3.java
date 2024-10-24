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

import java.util.Optional;

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

  protected static final String LOG_NAME = "MapBasedTypeCheck3";

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
    // do not reset if target typing had been used, as currently,
    // there is no target type
    if (getCtx4Ast().getContextOfExpression(expr).hasTargetType() &&
        getType4Ast().hasPartialTypeOfExpression(expr)) {
      Log.trace(
          expr.get_SourcePositionStart() + "-"
              + expr.get_SourcePositionEnd() + " typeof(): "
              + "no target type given, but had been given ("
              + getCtx4Ast().getContextOfExpression(expr)
              .getTargetType().printFullName()
              + "), using the cached result;",
          LOG_NAME
      );
    }
    if (!getType4Ast().hasPartialTypeOfExpression(expr)) {
      expr.accept(getTypeTraverser());
    }
    return getType4Ast().getPartialTypeOfExpr(expr);
  }

  @Override
  public SymTypeExpression _typeOf(
      ASTExpression expr,
      SymTypeExpression targetType
  ) {
    // if some other (or no) target type had been used before,
    // then something questionable is happening (this is not expected)
    Optional<SymTypeExpression> targetTypeOld =
        getCtx4Ast().getContextOfExpression(expr).hasTargetType() ?
            Optional.of(getCtx4Ast().getContextOfExpression(expr).getTargetType()) :
            Optional.empty();
    if (getType4Ast().hasPartialTypeOfExpression(expr) && targetTypeOld.isEmpty()) {
      Log.error("0xFD111 internal error: "
              + "called typeof() with target type " + targetType.printFullName()
              + ", however, typeof() for the same expression"
              + " had already been called without target type"
              + ", which is not expected;"
              + " A call to typeof() with the target type must be "
              + " before every call to typeof without target type"
              + ", such that the corresponding result can be cached."
              + System.lineSeparator()
              + "  1. Check the order of calls to typeof() (e.g., in CoCos)."
              + System.lineSeparator()
              + "  2. It is recommended to have a separate visitor"
              + " to call typeof() with target type before running"
              + " any other visitors relying on typeof()"
              + ", such that they can use the cached value.",
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd()
      );
      // need to reset in case the target type changes the typing in the AST
      getType4Ast().reset(expr);
      getCtx4Ast().reset(expr);
    }
    else if (getType4Ast().hasPartialTypeOfExpression(expr) &&
        targetTypeOld.isPresent() &&
        !targetTypeOld.get().deepEquals(targetType)
    ) {
      Log.error("0xFD112 internal error: "
              + "called typeof() with target type " + targetType.printFullName()
              + ", however, typeof() for the same expression"
              + " had already been called with target type "
              + targetTypeOld.get().printFullName()
              + ", which is not expected;"
              + " Every expression must have at most one target type,"
              + " as the typeof()-results are cached."
              + " For special cases such as modeling languages with variability, "
              + "it is suggested to either reset the maps"
              + " used in this TypeCheck implementation"
              + " or reset the TypeCheck altogether between runs.",
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd()
      );
      // need to reset in case the target type changes the typing in the AST
      getType4Ast().reset(expr);
      getCtx4Ast().reset(expr);
    }
    if (!getType4Ast().hasPartialTypeOfExpression(expr)) {
      getCtx4Ast().setTargetTypeOfExpression(expr, targetType);
      expr.accept(getTypeTraverser());
    }
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
