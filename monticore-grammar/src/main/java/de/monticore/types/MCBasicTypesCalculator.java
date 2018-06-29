package de.monticore.types;

import de.monticore.literals.literals._ast.ASTBooleanLiteral;
import de.monticore.mcexpressions._ast.ASTBooleanAndOpExpression;
import de.monticore.mcexpressions._ast.ASTBooleanOrOpExpression;
import de.monticore.mcexpressions._ast.ASTExpression;
import de.monticore.mcexpressions._ast.ASTNameExpression;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.monticore.types.mcbasictypes._ast.ASTPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTType;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor;

import java.util.Map;

public class MCBasicTypesCalculator {

  private final Map<String, Integer> variableTypes;

  public MCBasicTypesCalculator(Map<String,Integer> variableTypes) {
    this.variableTypes = variableTypes;
  }
  
  
  public boolean isBool(ASTPrimitiveType primitiveType) {
    return primitiveType.isBoolean();
  }


  public boolean isBool(ASTExpression expression) {
    System.out.println("Start");
    MCBasicTypesCalculatorVisitor visitor = new MCBasicTypesCalculatorVisitor(this);

    expression.accept(visitor);


    return visitor.isBool;
  }

  public boolean isBool(ASTBooleanOrOpExpression e) {
    System.out.println("OrOP");

    ASTExpression left = e.getLeftExpression();
    ASTExpression right = e.getRightExpression();


    MCBasicTypesCalculatorVisitor leftVisitor = new MCBasicTypesCalculatorVisitor(this);
    MCBasicTypesCalculatorVisitor rightVisitor = new MCBasicTypesCalculatorVisitor(this);

    left.accept(leftVisitor);
    right.accept(rightVisitor);


    return leftVisitor.isBool && rightVisitor.isBool;
  }


  public boolean isBool(ASTBooleanAndOpExpression e) {
    System.out.println("ANDOP");

    ASTExpression left = e.getLeftExpression();
    ASTExpression right = e.getRightExpression();


    MCBasicTypesCalculatorVisitor leftVisitor = new MCBasicTypesCalculatorVisitor(this);
    MCBasicTypesCalculatorVisitor rightVisitor = new MCBasicTypesCalculatorVisitor(this);

    left.accept(leftVisitor);
    right.accept(rightVisitor);


    return leftVisitor.isBool && rightVisitor.isBool;
  }


  public Boolean isBool(ASTBooleanLiteral lit) {
    System.out.println("LIT");
    return true;
  }

  public Boolean isBool(ASTNameExpression ex) {
    System.out.println("Name Expression-> use SymbolTable");
    return this.variableTypes.get(ex.getName()) == ASTConstantsMCBasicTypes.BOOLEAN;
  }



}
