package de.monticore.types.check;

import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor;
import de.monticore.types.typesymbols._symboltable.TypeSymbolLoader;

import java.util.Optional;

/**
 * Visitor for Derivation of SymType from MCBasicTypes
 * i.e. for
 *    types/MCBasicTypes.mc4
 */
public class SynthesizeSymTypeFromMCBasicTypes implements MCBasicTypesVisitor {
  
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
  
  // ---------------------------------------------------------- Storage result
  
  /**
   * Storage in the Visitor: result of the last endVisit.
   * This attribute is synthesized upward.
   */
  public Optional<SymTypeExpression> result;
  
  public Optional<SymTypeExpression> getResult() {
    return result;
  }
  
  public void init() {
    result = Optional.empty();
  }
  
  // ---------------------------------------------------------- Visting Methods

  /**
   * We use mainly endVisit, because the result is synthesized along the
   * tree, when walking upwards
   */

  public void endVisit(ASTMCPrimitiveType primitiveType) {
    SymTypeConstant typeConstant =
            SymTypeExpressionFactory.createTypeConstant(primitiveType.printType(MCBasicTypesMill.mcBasicTypesPrettyPrinter()));
    result = Optional.of(typeConstant);
  }
  
  public void endVisit(ASTMCVoidType voidType) {
    result = Optional.of(SymTypeExpressionFactory.createTypeVoid());
  }
  
  /**
   * Asks the SymTypeExpressionFactory to create the correct Type
   * Here: the Argument may be qualified Type object, but that allows only primitives, such as "int" or
   * boxed versions, such as "java.lang.Boolean"
   * This are the only qualified Types that may occur.
   * In particular: This method needs to be overriden when real qualified Types occur.
   * @param qType
   */
  public void endVisit(ASTMCQualifiedType qType) {
    // Otherwise the Visitor is applied to the wrong AST (and an internal error 0x893F62 is issued
    SymTypeExpression tex =
        SymTypeExpressionFactory.createTypeObject(new TypeSymbolLoader(qType.printType(MCBasicTypesMill.mcBasicTypesPrettyPrinter()), qType.getEnclosingScope()));
  }
  
  public void endVisit(ASTMCReturnType rType) {
    // result is pushed upward (no change)
  }
  
}
