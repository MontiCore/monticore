package de.monticore.types2;

import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsVisitor;
import de.monticore.expressions.bitexpressions._visitor.BitExpressionsVisitor;
import de.monticore.expressions.combinebitandassignmentexpressions._visitor.CombineBitAndAssignmentExpressionsDelegatorVisitor;
import de.monticore.expressions.combinebitandassignmentexpressions._visitor.CombineBitAndAssignmentExpressionsVisitor;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsVisitor;
import de.monticore.literals.mcliteralsbasis._visitor.MCLiteralsBasisVisitor;

public class DeriveSymTypeOfCombineBitAndAssignmentExpressions extends CombineBitAndAssignmentExpressionsDelegatorVisitor {

  CombineBitAndAssignmentExpressionsDelegatorVisitor realThis = this;

  BitExpressionsVisitor bitExpressionsVisitor = new DeriveSymTypeOfBitExpressions();
  AssignmentExpressionsVisitor assignmentExpressionsVisitor = new DeriveSymTypeOfAssignmentExpressions();
  ExpressionsBasisVisitor expressionsBasisVisitor = new DeriveSymTypeOfExpression();
  MCLiteralsBasisVisitor mcLiteralsBasisVisitor = new DeriveSymTypeOfLiterals();
  MCCommonLiteralsVisitor mcCommonLiteralsVisitor = new DeriveSymTypeOfMCCommonLiterals();

  public CombineBitAndAssignmentExpressionsDelegatorVisitor getRealThis(){
    return realThis;
  }


}
