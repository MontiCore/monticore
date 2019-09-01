package de.monticore.types2;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.ASTMCVoidType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.typesymbols._ast.ASTType;
import de.se_rwth.commons.logging.Log;

/**
 * This class is intended to provide typeChecking functionality.
 * It is designed as static singleton, allowing to
 * plug-in the appropriate implementation, dealing with
 * all Expression, Literal and Type-classes that are used in the
 * respective project.
 * (It is thus configure along three dimensions:
 *    Literals
 *    Expressions
 *    Types)
 * This interface only knows about the two top Level grammars, such as
 * MCBasicTypes, ExpressionsBasis (it includes their main Nonterminals
 * in the signature)
 */
public abstract class TypeCheck {
  
  /**
   * singleton instance (hidden from outside)
   */
  protected static TypeCheck typeCheck;
  
  /**
   * gives back the instance
   * (it needs to be initialized first: there is no default, but an exception Msg!)
   */
  protected static TypeCheck getTypeCheck() {
    if (typeCheck == null) {
      Log.error("0xD34B3 TypeCheck used without initialization (static singleton). Fatal.");
      // if this happens, e.g. include TypeCheck.initMe(new XYTypeCheck()) in the initialisation phase
    }
    return typeCheck;
  }
  
  /**
   * used for initialization with a concrete TypeChecker
   *
   * @param a e.g. a minimal typecheck
   */
  public static void initMe(TypeCheck a) {
    typeCheck = a;
  }
  
  /**
   * Resets the initialization (rarely needed)
   */
  public static void reset() {
    typeCheck = null;
  }
  
  /*************************************************************************/
  
  /**
   * Function 1: extracting the SymTypeExpression from an AST Type
   * The SymTypeExpression is independent of the AST and can be stored in the SymTab etc.
   */
  public static SymTypeExpression symTypeFromAST(ASTMCType astMCType)  {
    return getTypeCheck()._symTypeFromAST(astMCType);
  }
  
  abstract protected  SymTypeExpression _symTypeFromAST(ASTMCType astMCType);
  
  
  /**
   * Function 1b: extracting the SymTypeExpression from the AST Type "void"
   * ("void" is not in the ASTMCType hierarchy, while it is included in the SymTypeExpressions)
   */
  public static SymTypeExpression symTypeFromAST(ASTMCVoidType astMCVoidType)  {
    return getTypeCheck()._symTypeFromAST(astMCVoidType);
  }

  protected SymTypeExpression _symTypeFromAST(ASTMCVoidType astMCVoidType) {
    return SymTypeExpressionFactory.createTypeVoid();
  }
  
  /**
   * Function 1c: extracting the SymTypeExpression from the AST MCReturnType
   * (MCReturnType is not in the ASTMCType hierarchy, while it is included in the SymTypeExpressions)
   */
  public static SymTypeExpression symTypeFromAST(ASTMCReturnType astMCReturnType)  {
    return getTypeCheck()._symTypeFromAST(astMCReturnType);
  }
  
  abstract protected SymTypeExpression _symTypeFromAST(ASTMCReturnType astMCReturnType);
  
  /**
   * Function 2: Get the SymTypeExpression from an Expression AST
   * This defines the Type that an Expression has.
   * Precondition:
   * Free Variables in the AST are being looked u through the Symbol Table that
   * needs to be in place; same for method calls etc.
   */
  public static SymTypeExpression typeOf(ASTExpression expr)  {
    return getTypeCheck()._typeOf(expr);
  }
  
  abstract protected  SymTypeExpression _typeOf(ASTExpression expr);
  
  
  /**
   * Function 3:
   * Given two SymTypeExpressions super, sub:
   * This function answers, whether sub is a subtype of super.
   * (This allows to store/use values of type "sub" at all positions of type "super".
   * Compatibility examples:
   *      compatible("int", "long")       (in all directions)
   *      compatible("long", "in")        (in all directions)
   *      compatible("double", "float")   (in all directions)
   *      compatible("Person", "Student") (uni-directional)
   *
   * Incompatible:
   *     !compatible("double", "int")   (in all directions)
   *
   * The concrete Typechecker has to decide on further issues, like
   *     !compatible("List<double>", "List<int>")  where e.g. Java and OCL/P differ
   *
   * @param sup  Super-Type
   * @param sub  Sub-Type (assignment-compatible to supertype?)
   *
   * TODO: Probably needs to be extended for free type-variable assignments
   * (because it may be that they get unified over time: e.g. Map<a,List<c>> and Map<long,b>
   * are compatible, by refining the assignments a-> long, b->List<c>
   */
  public static boolean compatible(SymTypeExpression sup, SymTypeExpression sub)  {
    return getTypeCheck()._compatible(sup,sub);
  }
  
  abstract protected boolean _compatible(SymTypeExpression sup, SymTypeExpression sub);
  
  
  /**
   * Function 4:
   * Checks whether the ASTExpression exp will result in a value that is of type, and
   * thus can be e.g. stored, sent, etc. Essentially exp needs to be of a subtype to
   * be assignement compatible.
   * @param exp  the Expression that shall be checked for a given type
   * @param type the Type it needs to have (e.g. the Type of a variable used for assignement, or the
   *             type of a channel where to send a value)
   */
  public static boolean isOfTypeForAssign(ASTExpression exp, SymTypeExpression type)  {
    return getTypeCheck()._isOfTypeForAssign(exp,type);
  }
  
  /**
   * Default implementation (should hold for all common cases)
   * @param exp  the Expression that shall be checked for a given type
   * @param type the Type it needs to have
   */
  protected boolean _isOfTypeForAssign(ASTExpression exp, SymTypeExpression type) {
    return _compatible( _typeOf(exp), type);
  };
  
}
