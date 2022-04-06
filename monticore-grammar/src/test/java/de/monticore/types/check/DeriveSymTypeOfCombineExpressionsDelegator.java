/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsTraverser;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;

/**
 * Delegator Visitor to test the combination of the grammars
 */
public class DeriveSymTypeOfCombineExpressionsDelegator implements IDerive {

  private CombineExpressionsWithLiteralsTraverser traverser;

  private DeriveSymTypeOfAssignmentExpressions deriveSymTypeOfAssignmentExpressions;

  private DeriveSymTypeOfCommonExpressions deriveSymTypeOfCommonExpressions;

  private DeriveSymTypeOfBitExpressions deriveSymTypeOfBitExpressions;

  private DeriveSymTypeOfExpression deriveSymTypeOfExpression;

  private DeriveSymTypeOfJavaClassExpressions deriveSymTypeOfJavaClassExpressions;

  private DeriveSymTypeOfLiterals deriveSymTypeOfLiterals;

  private DeriveSymTypeOfMCCommonLiterals deriveSymTypeOfMCCommonLiterals;

  private DeriveSymTypeOfCombineExpressions deriveSymTypeOfCombineExpressions;

  private SynthesizeSymTypeFromCombineExpressionsWithLiteralsDelegator synthesizer;


  private TypeCheckResult typeCheckResult = new TypeCheckResult();


  public DeriveSymTypeOfCombineExpressionsDelegator(){
    init();
  }

  public CombineExpressionsWithLiteralsTraverser getTraverser(){
    return traverser;
  }

  /**
   * set the last result of all calculators to the same object
   */
  public void setTypeCheckResult(TypeCheckResult typeCheckResult){
    deriveSymTypeOfAssignmentExpressions.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfMCCommonLiterals.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfCommonExpressions.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfExpression.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfLiterals.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfBitExpressions.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfJavaClassExpressions.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfCombineExpressions.setTypeCheckResult(typeCheckResult);
  }

  /**
   * initialize the typescalculator
   */
  public void init() {
    this.traverser = CombineExpressionsWithLiteralsMill.traverser();
    this.typeCheckResult = new TypeCheckResult();
    deriveSymTypeOfCommonExpressions = new DeriveSymTypeOfCommonExpressions();
    deriveSymTypeOfAssignmentExpressions = new DeriveSymTypeOfAssignmentExpressions();
    deriveSymTypeOfMCCommonLiterals = new DeriveSymTypeOfMCCommonLiterals();
    deriveSymTypeOfExpression = new DeriveSymTypeOfExpression();
    deriveSymTypeOfLiterals = new DeriveSymTypeOfLiterals();
    deriveSymTypeOfBitExpressions = new DeriveSymTypeOfBitExpressions();
    deriveSymTypeOfJavaClassExpressions = new DeriveSymTypeOfJavaClassExpressions();
    synthesizer = new SynthesizeSymTypeFromCombineExpressionsWithLiteralsDelegator();
    deriveSymTypeOfCombineExpressions = new DeriveSymTypeOfCombineExpressions(synthesizer);
    setTypeCheckResult(typeCheckResult);

    traverser.add4CommonExpressions(deriveSymTypeOfCommonExpressions);
    traverser.setCommonExpressionsHandler(deriveSymTypeOfCommonExpressions);
    traverser.add4AssignmentExpressions(deriveSymTypeOfAssignmentExpressions);
    traverser.setAssignmentExpressionsHandler(deriveSymTypeOfAssignmentExpressions);
    traverser.add4MCCommonLiterals(deriveSymTypeOfMCCommonLiterals);
    traverser.add4ExpressionsBasis(deriveSymTypeOfExpression);
    traverser.setExpressionsBasisHandler(deriveSymTypeOfExpression);
    traverser.add4MCLiteralsBasis(deriveSymTypeOfLiterals);
    traverser.add4BitExpressions(deriveSymTypeOfBitExpressions);
    traverser.setBitExpressionsHandler(deriveSymTypeOfBitExpressions);
    traverser.add4JavaClassExpressions(deriveSymTypeOfJavaClassExpressions);
    traverser.setJavaClassExpressionsHandler(deriveSymTypeOfJavaClassExpressions);
    traverser.add4CombineExpressionsWithLiterals(deriveSymTypeOfCombineExpressions);
    traverser.setCombineExpressionsWithLiteralsHandler(deriveSymTypeOfCombineExpressions);
  }

  @Override
  public TypeCheckResult deriveType(ASTLiteral lit) {
    typeCheckResult.reset();
    lit.accept(traverser);
    return typeCheckResult.copy();
  }

  @Override
  public TypeCheckResult deriveType(ASTExpression expr){
    typeCheckResult.reset();
    expr.accept(traverser);
    return typeCheckResult.copy();
  }

}
