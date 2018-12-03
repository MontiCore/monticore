package de.monticore.types;

import de.monticore.literals.literals._ast.ASTBooleanLiteral;
import de.monticore.mcexpressions._ast.*;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;

import java.util.Map;

public class MCBasicTypesCalculator {

  private final Map<String, Integer> variableTypes;

  public MCBasicTypesCalculator(Map<String,Integer> variableTypes) {
    this.variableTypes = variableTypes;
  }
  
  
  public boolean isBool(ASTMCPrimitiveType primitiveType) {
    return primitiveType.isBoolean();
  }


  public boolean isBool(ASTExpression expression) {

    MCBasicTypesBooleanCalculatorVisitor visitor = new MCBasicTypesBooleanCalculatorVisitor(this);

    expression.accept(visitor);

    return visitor.isBool;
  }

  public boolean isBool(ASTBooleanOrOpExpression e) {
    ASTExpression left = e.getLeftExpression();
    ASTExpression right = e.getRightExpression();

    MCBasicTypesBooleanCalculatorVisitor leftVisitor = new MCBasicTypesBooleanCalculatorVisitor(this);
    MCBasicTypesBooleanCalculatorVisitor rightVisitor = new MCBasicTypesBooleanCalculatorVisitor(this);

    left.accept(leftVisitor);
    right.accept(rightVisitor);

    return leftVisitor.isBool && rightVisitor.isBool;
  }


  public boolean isBool(ASTBooleanAndOpExpression e) {

    ASTExpression left = e.getLeftExpression();
    ASTExpression right = e.getRightExpression();

    MCBasicTypesBooleanCalculatorVisitor leftVisitor = new MCBasicTypesBooleanCalculatorVisitor(this);
    MCBasicTypesBooleanCalculatorVisitor rightVisitor = new MCBasicTypesBooleanCalculatorVisitor(this);

    left.accept(leftVisitor);
    right.accept(rightVisitor);

    return leftVisitor.isBool && rightVisitor.isBool;
  }


  public Boolean isBool(ASTBooleanLiteral lit) {
    return true;
  }

  public Boolean isBool(ASTNameExpression ex) {

    if(this.variableTypes.get(ex.getName())!=null)
      return this.variableTypes.get(ex.getName()) == ASTConstantsMCBasicTypes.BOOLEAN;
    else
      return false;
  }

  public Boolean isBool(ASTCallExpression ex) {

    MCBasicTypesBooleanCalculatorVisitor visitor = new MCBasicTypesBooleanCalculatorVisitor(this);
    ex.getExpression().accept(visitor);

    return visitor.isBool;
  }

}
