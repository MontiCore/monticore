package de.monticore.typescalculator;

import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisScope;
import de.monticore.expressions.prettyprint2.ExpressionsBasisPrettyPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.typescalculator.combineexpressions._ast.ASTFoo;
import de.monticore.typescalculator.combineexpressions._visitor.CombineExpressionsDelegatorVisitor;
import de.monticore.typescalculator.combineexpressions._visitor.CombineExpressionsVisitor;
import de.se_rwth.commons.logging.Log;

public class CombineExpressionsTypesCalculator extends CombineExpressionsDelegatorVisitor {

  private CombineExpressionsDelegatorVisitor realThis;

  private ASTMCType result;

  private LiteralTypeCalculator literalsVisitor;

  private ExpressionsBasisScope scope;

  private CommonExpressionsWithLiteralsTypesCalculator commonExpressionsWithLiteralsTypesCalculator;

  private AssignmentExpressionsWithLiteralsTypesCalculator assignmentExpressionsWithLiteralsTypesCalculator;

  private AssignmentExpressionTypesCalculator assignmentExpressionTypesCalculator;

  private CommonExpressionTypesCalculator commonExpressionTypesCalculator;


  public CombineExpressionsTypesCalculator(ExpressionsBasisScope scope, LiteralTypeCalculator literalsVisitor){
    this.realThis=this;
    this.scope=scope;
    this.literalsVisitor=literalsVisitor;
    commonExpressionsWithLiteralsTypesCalculator = new CommonExpressionsWithLiteralsTypesCalculator();
    commonExpressionsWithLiteralsTypesCalculator.setScope(scope);
    commonExpressionsWithLiteralsTypesCalculator.setLiteralsVisitor(literalsVisitor);
    setCommonExpressionsWithLiteralsVisitor(commonExpressionsWithLiteralsTypesCalculator);

    assignmentExpressionsWithLiteralsTypesCalculator = new AssignmentExpressionsWithLiteralsTypesCalculator();
    assignmentExpressionsWithLiteralsTypesCalculator.setLiteralsVisitor(literalsVisitor);
    assignmentExpressionsWithLiteralsTypesCalculator.setScope(scope);
    setAssignmentExpressionsWithLiteralsVisitor(assignmentExpressionsWithLiteralsTypesCalculator);

    commonExpressionTypesCalculator = new CommonExpressionTypesCalculator();
    commonExpressionTypesCalculator.setScope(scope);
    commonExpressionTypesCalculator.setLiteralsVisitor(literalsVisitor);
    setCommonExpressionsVisitor(commonExpressionTypesCalculator);

    assignmentExpressionTypesCalculator = new AssignmentExpressionTypesCalculator();
    assignmentExpressionTypesCalculator.setLiteralsVisitor(literalsVisitor);
    assignmentExpressionTypesCalculator.setScope(scope);
    setAssignmentExpressionsVisitor(assignmentExpressionTypesCalculator);
  }

  @Override
  public CombineExpressionsDelegatorVisitor getRealThis(){
    return realThis;
  }

  public ASTMCType getResult(){
    if(commonExpressionsWithLiteralsTypesCalculator.getResult()!=null){
      return commonExpressionsWithLiteralsTypesCalculator.getResult();
    }
    if(assignmentExpressionsWithLiteralsTypesCalculator.getResult()!=null){
      return assignmentExpressionsWithLiteralsTypesCalculator.getResult();
    }
    if(assignmentExpressionTypesCalculator.getResult()!=null){
      return assignmentExpressionTypesCalculator.getResult();
    }
    if(commonExpressionTypesCalculator.getResult()!=null){
      return commonExpressionTypesCalculator.getResult();
    }
    return null;
  }

  public void setScope(ExpressionsBasisScope scope){
    this.scope=scope;
  }

  public void setLiteralsVisitor(LiteralTypeCalculator literalsVisitor){
    this.literalsVisitor=literalsVisitor;
  }

}
