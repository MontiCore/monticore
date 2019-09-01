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
   * Configuration: Visitor for Function 1:
   * extracting the SymTypeExpression from an AST Type.
   * May be SynthesizeSymTypeFromMCBasicTypes or any subclass;
   */
  protected SynthesizeSymTypeFromMCBasicTypes synthesizeSymType;
  
  /**
   * Configuration as state:
   * synthesizeSymType definee, which AST Types are mapped (and how)
   * TODO BR: Weitere Konfigutionen folgen: jeweils zu einer der Expr,Lits,Types ...
   * TODO: Kann sein, dass alles ausgelagert wird: dann ist
   * keine Subklasse "Basic" Mehr notwendig --> Zusammenlegen mit TypCheck??
   */
  public TypeCheckBasic(SynthesizeSymTypeFromMCBasicTypes synthesizeSymType) {
    this.synthesizeSymType = synthesizeSymType;
  }
  
  /**
   * Predefined minimal Configuration as default:
   * (cannot handle mire than only the top elements)
   */
  public TypeCheckBasic() {
    synthesizeSymType = new SynthesizeSymTypeFromMCBasicTypes();
  }
  
  
  /*************************************************************************/
  
  /**
   * Function 1: extracting the SymTypeExpression from an AST Type
   * The SymTypeExpression is independent of the AST and can be stored in the SymTab etc.
   */
  @Override
  public SymTypeExpression symTypeFromAST(ASTMCType astMCType) {
    synthesizeSymType.init();
    astMCType.accept(synthesizeSymType);
    Optional<SymTypeExpression> result = synthesizeSymType.getResult();
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
  @Override
  public SymTypeExpression symTypeFromAST(ASTMCReturnType astMCReturnType) {
    synthesizeSymType.init();
    astMCReturnType.accept(synthesizeSymType);
    Optional<SymTypeExpression> result = synthesizeSymType.getResult();
    if(!result.isPresent()) {
      Log.error("0xE9FD5 Internal Error: No SymType for return type: "
              + astMCReturnType.printType() + ". Probably TypeCheck mis-configured.");
    }
    return result.get();
  }

  
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
