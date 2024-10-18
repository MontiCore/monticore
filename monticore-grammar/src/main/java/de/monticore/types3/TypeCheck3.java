package de.monticore.types3;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.ASTMCVoidType;
import de.se_rwth.commons.logging.Log;

/**
 * This class is intended to provide type-checking functionality.
 * <p>
 * It is designed as functional class (no state),
 * allowing to plug in the appropriate implementation through subclasses.
 * Those subclasses can deal with variants of
 * Expression, Literal, and Type-classes
 * that are used in the respective project.
 * This class only knows about the three top Level grammars,
 * ExpressionsBasis, MCLiteralsBasis, and MCBasicTypes,
 * because it includes their main non-terminals in the signature.
 * <p>
 * The provided methods return the SymTypeExpression
 * that corresponds to the provided AST-node.
 * SymTypeExpressions are independent of the AST
 * and can be stored in the symbol table, etc.
 */
public abstract class TypeCheck3 {

  //---------------------Field--------------------//

  /**
   * Static delegate responsible for ALL methods of this class;
   * No further fields ought to be added to this class.
   * <p>
   * This has to be initialized by an implementing subclass.
   */
  protected static TypeCheck3 delegate;

  //---------------------Types--------------------//

  /**
   * Extracts the SymTypeExpression corresponding to the ASTMCType.
   */
  public static SymTypeExpression symTypeFromAST(ASTMCType mcType) {
    return getDelegate()._symTypeFromAST(mcType);
  }

  /**
   * Extracts the SymTypeExpression corresponding to "void".
   * "void" is not part of the ASTMCType-Hierarchy,
   * while it is included in the SymTypeExpressions.
   */
  public static SymTypeExpression symTypeFromAST(
      ASTMCVoidType mcVoidType
  ) {
    return getDelegate()._symTypeFromAST(mcVoidType);
  }

  /**
   * Extracts the SymTypeExpression corresponding to the return type.
   * ASTMCReturnType is not part of the ASTMCType-Hierarchy,
   * while it is included in the SymTypeExpressions.
   */
  public static SymTypeExpression symTypeFromAST(
      ASTMCReturnType mcReturnType
  ) {
    return getDelegate()._symTypeFromAST(mcReturnType);
  }

  /**
   * Extracts the SymTypeExpression corresponding to the ASTMCQualifiedName.
   */
  public static SymTypeExpression symTypeFromAST(
      ASTMCQualifiedName mcQualifiedName
  ) {
    return getDelegate()._symTypeFromAST(mcQualifiedName);
  }

  //---------------------Expressions--------------------//

  /**
   * Derive the type of the expression.
   * Each typeable expression, in a fixed context, has exactly one type.
   * <p>
   * Whenever possible, instead use
   * {@link #typeOf(ASTExpression, SymTypeExpression)}.
   */
  public static SymTypeExpression typeOf(ASTExpression expr) {
    return getDelegate()._typeOf(expr);
  }

  /**
   * Derives the type of the expression,
   * using the expected target type as additional information.
   * <p>
   * E.g.: List<int> myList = []; // [] returns an empty list
   * Here, the type of [] is to be calculated.
   * Without target type, it is known that a List is returned.
   * However, the type argument of List is unknown.
   * Using the target type List<int> as additional information,
   * allows to additionally derive the type argument int,
   * resulting in deriving List<int> to be the expression []'s type.
   * <p>
   * Note: it is up to the concrete implementation,
   * whether the target type is used.
   */
  public static SymTypeExpression typeOf(
      ASTExpression expr,
      SymTypeExpression targetType
  ) {
    return getDelegate()._typeOf(expr, targetType);
  }

  /**
   * Derives the type of the literal.
   */
  public static SymTypeExpression typeOf(ASTLiteral lit) {
    return getDelegate()._typeOf(lit);
  }

  //---------------------Internals--------------------//

  protected abstract SymTypeExpression _symTypeFromAST(
      ASTMCType mcType
  );

  protected abstract SymTypeExpression _symTypeFromAST(
      ASTMCVoidType mcVoidType
  );

  protected abstract SymTypeExpression _symTypeFromAST(
      ASTMCReturnType mcReturnType
  );

  protected abstract SymTypeExpression _symTypeFromAST(
      ASTMCQualifiedName mcQualifiedName
  );

  protected abstract SymTypeExpression _typeOf(
      ASTExpression expr
  );

  protected SymTypeExpression _typeOf(
      ASTExpression expr,
      SymTypeExpression targetType
  ) {
    // default behavior: the target type is simply ignored
    return _typeOf(expr);
  }

  protected abstract SymTypeExpression _typeOf(
      ASTLiteral lit
  );

  protected static void setDelegate(TypeCheck3 delegate) {
    TypeCheck3.delegate = delegate;
  }

  protected static TypeCheck3 getDelegate() {
    if (TypeCheck3.delegate == null) {
      Log.errorInternal("0xFD777 internal error: "
          + "TypeCheck has not been initialized."
          + " Please refer to the TypeCheck3 subclass(es) of your language."
      );
    }
    return TypeCheck3.delegate;
  }

}
