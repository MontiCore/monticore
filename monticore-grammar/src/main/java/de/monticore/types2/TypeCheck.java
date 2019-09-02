package de.monticore.types2;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.ASTMCVoidType;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * This class is intended to provide typeChecking functionality.
 * It is designed as functional class (no state), allowing to
 * plug-in the appropriate implementation through subclasses,
 * Those subclasses can deal with variants of Expression, Literal
 * and Type-classes that are used in the respective project.
 * (It is thus configure along three dimensions:
 *    Literals
 *    Expressions
 *    Types)
 * This class only knows about the thre top Level grammars:
 * MCBasicTypes, ExpressionsBasis and MCLiteralsBasis, because it includes their
 * main NonTerminals in the signature.
 */
public class TypeCheck {
  
  /**
   * Configuration: Visitor for Function 1:
   * Synthesizing the SymTypeExpression from an AST Type.
   * May also be of a subclass;
   */
  protected SynthesizeSymTypeFromMCBasicTypes synthesizeSymType;
  
  /**
   * Configuration: Visitor for Function 2:
   * Deriving the SymTypeExpression from an AST Value - Expression.
   * May also be of a subclass;
   */
  protected DeriveSymTypeOfLiterals deriveSymTypeOfLiteral;
  
  /**
   * Configuration as state:
   * @param synthesizeSymType defines, which AST Types are mapped (and how)
   * @param deriveSymTypeOfLiteral defines, which AST Literals are handled
   *                               through the Expression recognition
   */
  public TypeCheck(SynthesizeSymTypeFromMCBasicTypes synthesizeSymType, DeriveSymTypeOfLiterals deriveSymTypeOfLiteral) {
    this.synthesizeSymType = synthesizeSymType;
    this.deriveSymTypeOfLiteral = deriveSymTypeOfLiteral;
  }
  
  public TypeCheck(SynthesizeSymTypeFromMCBasicTypes synthesizeSymType) {
    this.synthesizeSymType = synthesizeSymType;
  }
  
  /**
   * Predefined minimal Configuration as default:
   * (cannot handle mire than only the top elements)
   */
  public TypeCheck() {
    synthesizeSymType = new SynthesizeSymTypeFromMCBasicTypes();
    deriveSymTypeOfLiteral = new DeriveSymTypeOfLiterals();
  }
  
  /*************************************************************************/
  
  /**
   * Function 1: extracting the SymTypeExpression from an AST Type
   * The SymTypeExpression is independent of the AST and can be stored in the SymTab etc.
   */
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
   * Function 1b: extracting the SymTypeExpression from the AST Type "void"
   * ("void" is not in the ASTMCType hierarchy, while it is included in the SymTypeExpressions)
   */
  public SymTypeExpression symTypeFromAST(ASTMCVoidType astMCVoidType)  {
    return SymTypeExpressionFactory.createTypeVoid();
  }
  
  /**
   * Function 1c: extracting the SymTypeExpression from the AST MCReturnType
   * (MCReturnType is not in the ASTMCType hierarchy, while it is included in the SymTypeExpressions)
   */
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
   * Function 2: Derive the SymTypeExpression from an Expression AST
   * This defines the Type that an Expression has.
   * Precondition:
   * Free Variables in the AST are being looked u through the Symbol Table that
   * needs to be in place; same for method calls etc.
   */
  public SymTypeExpression typeOf(ASTExpression expr) {
    // TODO
    return null;
  }
  
  /**
   * Function 2b: Derive the SymTypeExpression of a Literal
   * This defines the Type that a Literal has and will be used to
   * determine the Type of Expressions.
   */
  public SymTypeExpression typeOf(ASTLiteral lit) {
    deriveSymTypeOfLiteral.init();
    Optional<SymTypeExpression> result = deriveSymTypeOfLiteral.calculateType(lit);
    if(!result.isPresent()) {
      Log.error("0xED670 Internal Error: No Type for Literal " + lit
              + " Probably TypeCheck mis-configured.");
    }
    if (!result.isPresent()) {
      Log.error("0xE11E0 Internal Error: No-Type for literal " +
              lit.toString() + ". TypeCheck mis-configured?");
    }
    return result.get();
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
  public boolean compatible(SymTypeExpression sup, SymTypeExpression sub) {
    // TODO
    return false;
  }
  
  /*************************************************************************/
  
  /**
   * Function 4:
   * Checks whether the ASTExpression exp will result in a value that is of type, and
   * thus can be e.g. stored, sent, etc. Essentially exp needs to be of a subtype to
   * be assignment compatible.
   * (as it is combined from other functions, it need not be overwritten)
   * @param exp  the Expression that shall be checked for a given type
   * @param type the Type it needs to have (e.g. the Type of a variable used for assignment, or the
   *             type of a channel where to send a value)
   */
  public boolean isOfTypeForAssign(SymTypeExpression type, ASTExpression exp) {
    return compatible(  type, typeOf(exp));
  }
  
  
}
