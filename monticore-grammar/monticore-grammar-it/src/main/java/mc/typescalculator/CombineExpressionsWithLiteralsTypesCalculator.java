/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mccommonliterals._ast.ASTSignedLiteral;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.types.check.*;
import mc.typescalculator.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsDelegatorVisitor;

import java.util.Optional;

public class CombineExpressionsWithLiteralsTypesCalculator extends CombineExpressionsWithLiteralsDelegatorVisitor implements ITypesCalculator {

  private CombineExpressionsWithLiteralsDelegatorVisitor realThis;

  private DeriveSymTypeOfAssignmentExpressions assignmentExpressionTypesCalculator;

  private DeriveSymTypeOfCommonExpressions commonExpressionTypesCalculator;

  private DeriveSymTypeOfExpression expressionsBasisTypesCalculator;

  private DeriveSymTypeOfBitExpressions deriveSymTypeOfBitExpressions;

  private DeriveSymTypeOfLiterals deriveSymTypeOfLiterals;

  private DeriveSymTypeOfMCCommonLiterals commonLiteralsTypesCalculator;

  private TypeCheckResult typeCheckResult = new TypeCheckResult();


  public CombineExpressionsWithLiteralsTypesCalculator(){
    this.realThis=this;
    commonExpressionTypesCalculator = new DeriveSymTypeOfCommonExpressions();
    commonExpressionTypesCalculator.setTypeCheckResult(typeCheckResult);
    setCommonExpressionsVisitor(commonExpressionTypesCalculator);

    deriveSymTypeOfBitExpressions = new DeriveSymTypeOfBitExpressions();
    setBitExpressionsVisitor(deriveSymTypeOfBitExpressions);

    assignmentExpressionTypesCalculator = new DeriveSymTypeOfAssignmentExpressions();
    assignmentExpressionTypesCalculator.setTypeCheckResult(typeCheckResult);
    setAssignmentExpressionsVisitor(assignmentExpressionTypesCalculator);

    expressionsBasisTypesCalculator = new DeriveSymTypeOfExpression();
    expressionsBasisTypesCalculator.setTypeCheckResult(typeCheckResult);
    setExpressionsBasisVisitor(expressionsBasisTypesCalculator);
  
    DeriveSymTypeOfLiterals deriveSymTypeOfLiterals = new DeriveSymTypeOfLiterals();
    deriveSymTypeOfLiterals.setTypeCheckResult(typeCheckResult);
    setMCLiteralsBasisVisitor(deriveSymTypeOfLiterals);
    this.deriveSymTypeOfLiterals = deriveSymTypeOfLiterals;

    commonLiteralsTypesCalculator = new DeriveSymTypeOfMCCommonLiterals();
    commonExpressionTypesCalculator.setTypeCheckResult(typeCheckResult);
    setMCCommonLiteralsVisitor(commonLiteralsTypesCalculator);

    setTypeCheckResult(typeCheckResult);
  }

  public Optional<SymTypeExpression> calculateType(ASTExpression e){
    e.accept(realThis);
    Optional<SymTypeExpression> last = Optional.empty();
    if (typeCheckResult.isPresentCurrentResult()) {
      last = Optional.ofNullable(typeCheckResult.getCurrentResult());
    }
    typeCheckResult.reset();
    return last;
  }

  @Override
  public Optional<SymTypeExpression> calculateType(ASTLiteral lit) {
    lit.accept(realThis);
    Optional<SymTypeExpression> last = Optional.empty();
    if (typeCheckResult.isPresentCurrentResult()) {
      last = Optional.ofNullable(typeCheckResult.getCurrentResult());
    }
    typeCheckResult.reset();
    return last;
  }

  @Override
  public Optional<SymTypeExpression> calculateType(ASTSignedLiteral lit) {
    lit.accept(realThis);
    Optional<SymTypeExpression> last = Optional.empty();
    if (typeCheckResult.isPresentCurrentResult()) {
      last = Optional.ofNullable(typeCheckResult.getCurrentResult());
    }
    typeCheckResult.reset();
    return last;
  }

  @Override
  public void init() {
    deriveSymTypeOfLiterals = new DeriveSymTypeOfLiterals();
    deriveSymTypeOfBitExpressions = new DeriveSymTypeOfBitExpressions();
    commonLiteralsTypesCalculator = new DeriveSymTypeOfMCCommonLiterals();
    commonExpressionTypesCalculator = new DeriveSymTypeOfCommonExpressions();
    assignmentExpressionTypesCalculator = new DeriveSymTypeOfAssignmentExpressions();
    expressionsBasisTypesCalculator = new DeriveSymTypeOfExpression();
    setTypeCheckResult(typeCheckResult);
  }

  @Override
  public CombineExpressionsWithLiteralsDelegatorVisitor getRealThis(){
    return realThis;
  }


  public void setTypeCheckResult(TypeCheckResult typeCheckResult){
    this.typeCheckResult = typeCheckResult;
    assignmentExpressionTypesCalculator.setTypeCheckResult(typeCheckResult);
    commonExpressionTypesCalculator.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfBitExpressions.setTypeCheckResult(typeCheckResult);
    expressionsBasisTypesCalculator.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfLiterals.setTypeCheckResult(typeCheckResult);
    commonLiteralsTypesCalculator.setTypeCheckResult(typeCheckResult);
  }
}
