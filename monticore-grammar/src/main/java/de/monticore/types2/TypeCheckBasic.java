package de.monticore.types2;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.mcbasics._visitor.MCBasicsVisitor;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * TypeCheckBasic implements the basic TypeChecks
 * i.e. for
 *    expressions/ExpressionsBasis.mc4
 *    literals/MCLiteralsBasis.mc4
 *    types/MCBasicTypes.mc4
 * This implementation will rarely be used, but serves as starter for extensions.
 */
public class TypeCheckBasic extends TypeCheck implements MCBasicTypesVisitor {
  
  /**
   * Empty: it has no state.
   * TODO: Kann sein, dass wir einzelne Teile auslagern und ihn so
   * konfigurierbar machen (jeweils zu einer der Expr,Lits,Types ...
   */
  public TypeCheckBasic() { }
  
  /*************************************************************************/
  
  /**
   * Function 1: extracting the SymTypeExpression from an AST Type
   * The SymTypeExpression is independent of the AST and can be stored in the SymTab etc.
   */
  @Override
  public SymTypeExpression symTypeFromAST(ASTMCType astMCType) {
    result = Optional.empty();
    astMCType.accept(getRealThis());
    if(!result.isPresent()) {
      Log.error("0xE9FD4 Internal Error: No SymType for: "
              + astMCType.printType() + ". Probably TypeCheck mis-configured.");
    }
    return result.get();
  }
  
  /**
   * Function 1c: extracting the SymTypeExpression from the AST MCReturnType
   * (MCReturnType is not in the ASTMCType hierarchy, while it is included in the SymTypeExpressions)
   */
  public SymTypeExpression symTypeFromAST(ASTMCReturnType astMCReturnType) {
    result = Optional.empty();
    astMCReturnType.accept(getRealThis());
    if(!result.isPresent()) {
      Log.error("0xE9FD5 Internal Error: No SymType for: "
              + astMCReturnType.printType() + ". Probably TypeCheck mis-configured.");
    }
    return result.get();
  }

  /**
   * Using the visitor functionality to calculate the SymType Expression
   */

  // ----------------------------------------------------------  realThis start
  // setRealThis, getRealThis are necessary to make the visitor compositional
  //
  // (the Vistors are then composed using theRealThis Pattern)
  //
  MCBasicTypesVisitor realThis = this;
  
  @Override
  public void setRealThis(MCBasicTypesVisitor realThis) {
    this.realThis = realThis;
  }
  
  @Override
  public MCBasicTypesVisitor getRealThis() {
    return realThis;
  }
  // ---------------------------------------------------------- realThis end
  
  /**
   * Storage in the Visitor: result of the last endVisit
   */
  public Optional<SymTypeExpression> result;
  
  /**
   * We use mainly endVisit, because the result is synthesized along the
   * tree, when walking upwards
   */

  public void endVisit(ASTMCPrimitiveType primitiveType) {
    SymTypeConstant typeConstant =
            SymTypeExpressionFactory.createTypeConstant(primitiveType.getName());
    result = Optional.of(typeConstant);
  }
  
  public void endVisit(ASTMCVoidType voidType) {
    result = Optional.of(SymTypeExpressionFactory.createTypeVoid());
  }
  
  public void endVisit(ASTMCQualifiedType qType) {
    result = Optional.of(SymTypeExpressionFactory.createTypeConstant(qType.getName()));
  }
  
  public void endVisit(ASTMCReturnType rType) {
    // result is pushed upward (no change)
  }
  
  // -------------------------------------------------------------------- visitor end
  
  /*************************************************************************/
  
  /**
   * Function 2: Get the SymTypeExpression from an Expression AST
   * This defines the Type that an Expression has.
   * Precondition:
   * Free Variables in the AST are being looked u through the Symbol Table that
   * needs to be in place; same for method calls etc.
   */
  @Override
  public SymTypeExpression typeOf(ASTExpression expr) {
    // TODO
    return null;
  }
  
  
  /*************************************************************************/
  
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
   *     !compatible("void",   "int")
   *
   * @param sup  Super-Type
   * @param sub  Sub-Type (assignment-compatible to supertype?)
   */
  @Override
  public boolean compatible(SymTypeExpression sup, SymTypeExpression sub) {
    // TODO
     return false;
  }
  
  
}
