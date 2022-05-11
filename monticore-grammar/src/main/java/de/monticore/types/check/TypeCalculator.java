/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.ASTMCVoidType;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.se_rwth.commons.logging.Log;

import static de.monticore.types.check.TypeCheck.compatible;

public class TypeCalculator {

  /**
   * Configuration: Visitor for Function 1:
   * Synthesizing the SymTypeExpression from an AST Type.
   * May also be of a subclass;
   */
  protected AbstractSynthesize iSynthesize;

  /**
   * Configuration: Visitor for Function 2b:
   * Deriving the SymTypeExpression from an AST Value - Literal.
   * May also be of a subclass;
   */
  protected AbstractDerive iDerive;


  /**
   * Configuration as state:
   * @param synthesizeSymType defines, which AST Types are mapped (and how)
   * @param  iDerive defines, which AST Literals are handled
   *                               through the Expression type recognition
   */
  public TypeCalculator(AbstractSynthesize synthesizeSymType,
                        AbstractDerive iDerive) {
    this.iSynthesize = synthesizeSymType;
    this.iDerive = iDerive;
  }

  /*************************************************************************/


  /**
   * Function 1: extracting the SymTypeExpression from an AST Type
   * The SymTypeExpression is independent of the AST and can be stored in the SymTab etc.
   *
   * Tests for this Function are combined in the Visitor tests
   * (SynthesizeSymType.*Types.*Test)
   */
  public SymTypeExpression symTypeFromAST(ASTMCType ast)
  {
    TypeCheckResult result = iSynthesize.synthesizeType(ast);
    if(!result.isPresentResult()) {
      Log.error("0xE9FD4 Internal Error: No SymType for: "
        + ast.printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()) + ". Probably TypeCheck mis-configured.");
    }
    return result.getResult();
  }


  /**
   * Function 1b: extracting the SymTypeExpression from the AST Type "void"
   * ("void" is not in the ASTMCType hierarchy, while it is included in the SymTypeExpressions)
   */
  public SymTypeExpression symTypeFromAST(ASTMCVoidType ast)
  {
    return SymTypeExpressionFactory.createTypeVoid();
  }


  /**
   * Function 1c: extracting the SymTypeExpression from the AST MCReturnType
   * (MCReturnType is not in the ASTMCType hierarchy, while it is included in the SymTypeExpressions)
   *
   * Tests for this Function are combined in the Visitor tests
   * (SynthesizeSymType.*Types.*Test)
   */
  public SymTypeExpression symTypeFromAST(ASTMCReturnType ast)
  {
    TypeCheckResult result = iSynthesize.synthesizeType(ast);
    if(!result.isPresentResult()) {
      Log.error("0xE9FD9 Internal Error: No SymType for return type: "
        + ast.printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter())
        + ". Probably TypeCheck mis-configured.");
    }
    return result.getResult();
  }


  /**
   * Function 1d: extracting the SymTypeExpression from the AST MCQualifiedName
   *
   * Tests for this Function are combined in the Visitor tests
   * (SynthesizeSymType.*Types.*Test)
   */
  public SymTypeExpression symTypeFromAST(ASTMCQualifiedName ast)
  {
    TypeCheckResult result = iSynthesize.synthesizeType(ast);
    if(!result.isPresentResult()) {
      Log.error("0xE9FD5 Internal Error: No SymType for MCQualifiedName: "
        + ast.getBaseName()
        + ". Probably TypeCheck mis-configured.");
    }
    return result.getResult();
  }


  /*************************************************************************/


  /**
   * Function 2: Derive the SymTypeExpression from an Expression AST
   * This defines the Type that an Expression has.
   * Precondition:
   * Free Variables in the AST are being looked u through the Symbol Table that
   * needs to be in place; same for method calls etc.
   */
  public SymTypeExpression typeOf(ASTExpression expr)
  {
    TypeCheckResult result = iDerive.deriveType(expr);
    if(!result.isPresentResult()) {
      Log.error("0xED680 Internal Error: No Type for Expression " + expr
        + " Probably TypeCheck mis-configured.");
    }
    return result.getResult();
  }


  /**
   * Function 2b: Derive the SymTypeExpression of a Literal
   * This defines the Type that a Literal has and will be used to
   * determine the Type of Expressions.
   *
   * Tests for this Function are combined in the Visitor tests
   * (DeriveSymType.*Literals.*Test)
   */
  public SymTypeExpression typeOf(ASTLiteral lit)
  {
    TypeCheckResult result = iDerive.deriveType(lit);
    if(!result.isPresentResult()) {
      Log.error("0xED670 Internal Error: No Type for Literal " + lit
        + " Probably TypeCheck mis-configured.");
    }
    return result.getResult();
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
  public boolean isOfTypeForAssign(SymTypeExpression type,
                                   ASTExpression exp)
  {
    return compatible(  type, typeOf(exp));
    // that is all what is needed
  }


}
