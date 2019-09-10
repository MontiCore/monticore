package de.monticore.types2;

import de.monticore.expressions.bitexpressions._ast.ASTLeftShiftExpression;
import de.monticore.expressions.bitexpressions._visitor.BitExpressionsVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class DeriveSymTypeOfBitExpressions extends DeriveSymTypeOfExpression implements BitExpressionsVisitor {

  // ----------------------------------------------------------  realThis start
  // setRealThis, getRealThis are necessary to make the visitor compositional
  //
  // (the Vistors are then composed using theRealThis Pattern)
  //
  BitExpressionsVisitor realThis = this;

  @Override
  public void setRealThis(BitExpressionsVisitor realThis) {
    this.realThis = realThis;
    super.realThis = realThis;
  }

  @Override
  public BitExpressionsVisitor getRealThis() {
    return realThis;
  }
  // ---------------------------------------------------------- realThis end

  // inherited:
  // public Optional<SymTypeExpression> result = Optional.empty();
  // protected DeriveSymTypeOfLiterals deriveLit;

  // ---------------------------------------------------------- Additional Visting Methods

  /** Overriding the generall error message to see that the error comes from this visitor
   */
  @Override
  public void endVisit(ASTExpression ex){
    // This general method is only called, if no specific exists,:
    // Should not happen.
    Log.error("0xEE771 Internal Error: No Type for expression " + ex.toString()
        + ". Probably TypeCheck mis-configured.");
  }

  /**********************************************************************************/

  @Override
  public void traverse(ASTLeftShiftExpression ex){
    ex.getLeft().accept(getRealThis());
    //caching result of left Expression
    SymTypeExpression leftResult = getResult().get();
    ex.getRight().accept(getRealThis());
    //caching result of right Expression
    SymTypeExpression rightResult = getResult().get();

    //LeftShift is only specified for int << int combinations
    if(leftResult.print().equals("int")&&rightResult.print().equals("int")){
      result = Optional.of(SymTypeExpressionFactory.createTypeConstant("int"));
    }
  }
}
