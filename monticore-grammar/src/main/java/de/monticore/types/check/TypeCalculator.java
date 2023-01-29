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

public class TypeCalculator implements ITypeRelations {

  /**
   * Configuration: Visitor for Function 1:
   * Synthesizing the SymTypeExpression from an AST Type.
   * May also be of a subclass;
   */
  protected ISynthesize iSynthesize;

  /**
   * Configuration: Visitor for Function 2b:
   * Deriving the SymTypeExpression from an AST Value - Literal.
   * May also be of a subclass;
   */
  protected IDerive iDerive;

  /**
   * Configuration: Implementation for Function 4:
   * Delegatee for ITypeRelations
   * Calculate relations of types.
   */
  protected ITypeRelations iTypeRelations;


  /**
   * Configuration as state:
   * @param synthesizeSymType defines, which AST Types are mapped (and how)
   * @param  iDerive defines, which AST Literals are handled
   *                               through the Expression type recognition
   * @param iTypeRelations defines, what the relations of types are,
   *                       this is delegated to, to provide the {@link ITypeRelations}
   */
  public TypeCalculator(ISynthesize synthesizeSymType,
                        IDerive iDerive,
                        ITypeRelations iTypeRelations) {
    this.iSynthesize = synthesizeSymType;
    this.iDerive = iDerive;
    this.iTypeRelations = iTypeRelations;
  }

  // only providing the old constructor to not break downstream languages
  // it is recommended to always provide the ITypeRelations implementation
  @Deprecated
  public TypeCalculator(ISynthesize iSynthesize, IDerive iDerive) {
    this(iSynthesize, iDerive, new TypeRelations());
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
        + ast.printType() + ". Probably TypeCheck mis-configured.");
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
        + ast.printType()
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

  @Override
  public boolean compatible(SymTypeExpression left, SymTypeExpression right) {
    return iTypeRelations.compatible(left, right);
  }

  @Override
  public boolean isSubtypeOf(SymTypeExpression subType, SymTypeExpression superType) {
    return iTypeRelations.isSubtypeOf(subType, superType);
  }

  @Override
  public int calculateInheritanceDistance(SymTypeExpression specific, SymTypeExpression general) {
    return iTypeRelations.calculateInheritanceDistance(specific, general);
  }

  @Override
  public int calculateInheritanceDistance(SymTypePrimitive specific, SymTypePrimitive general) {
    return iTypeRelations.calculateInheritanceDistance(specific, general);
  }

  @Override
  public boolean isBoolean(SymTypeExpression type) {
    return iTypeRelations.isBoolean(type);
  }

  @Override
  public boolean isInt(SymTypeExpression type) {
    return iTypeRelations.isInt(type);
  }

  @Override
  public boolean isDouble(SymTypeExpression type) {
    return iTypeRelations.isDouble(type);
  }

  @Override
  public boolean isFloat(SymTypeExpression type) {
    return iTypeRelations.isFloat(type);
  }

  @Override
  public boolean isLong(SymTypeExpression type) {
    return iTypeRelations.isLong(type);
  }

  @Override
  public boolean isChar(SymTypeExpression type) {
    return iTypeRelations.isChar(type);
  }

  @Override
  public boolean isShort(SymTypeExpression type) {
    return iTypeRelations.isShort(type);
  }

  @Override
  public boolean isByte(SymTypeExpression type) {
    return iTypeRelations.isByte(type);
  }

  @Override
  public boolean isVoid(SymTypeExpression type) {
    return iTypeRelations.isVoid(type);
  }

  @Override
  public boolean isString(SymTypeExpression type) {
    return iTypeRelations.isString(type);
  }
}
