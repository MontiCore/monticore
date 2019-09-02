package de.monticore.types2;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * Visitor for Derivation of SymType from MCBasicTypes
 * i.e. for
 *    types/MCBasicTypes.mc4
 */
public class SynthesizeSymTypeFromMCBasicTypes implements MCBasicTypesVisitor {
  
  public SynthesizeSymTypeFromMCBasicTypes() { }
  
  
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
  
  /**
   * We use mainly endVisit, because the result is synthesized along the
   * tree, when walking upwards
   */

  public void endVisit(ASTMCPrimitiveType primitiveType) {
    System.out.println("\nSynB XXX ev Primitive 1:" +result);
    SymTypeConstant typeConstant =
            SymTypeExpressionFactory.createTypeConstant(primitiveType.getName());
    result = Optional.of(typeConstant);
    System.out.println("\nSynB XXX ev Primitive End:" +typeConstant.print());
  }
  
  public void endVisit(ASTMCVoidType voidType)
  {
    result = Optional.of(SymTypeExpressionFactory.createTypeVoid());
  }
  
  public void endVisit(ASTMCQualifiedType qType) {
    result = Optional.of(SymTypeExpressionFactory.createTypeConstant(qType.getName()));
  }
  
  public void endVisit(ASTMCReturnType rType) {
    // result is pushed upward (no change)
  }
  
}
