package de.monticore.types;

import de.monticore.literals.literals._ast.ASTBooleanLiteral;
import de.monticore.mcexpressions._ast.ASTBooleanAndOpExpression;
import de.monticore.mcexpressions._ast.ASTBooleanOrOpExpression;
import de.monticore.mcexpressions._ast.ASTNameExpression;
import de.monticore.mcexpressions._visitor.MCExpressionsVisitor;


public class MCBasicTypesCalculatorVisitor implements MCExpressionsVisitor {

  public Boolean isBool = false;


  public MCBasicTypesCalculator basicTypesCalculator;

  MCBasicTypesCalculatorVisitor(MCBasicTypesCalculator calc) {
    this.basicTypesCalculator = calc;
  }

  @Override
  public void handle(ASTBooleanOrOpExpression ex) {
    System.out.println("Visit OrOpEx");
    this.isBool = this.basicTypesCalculator.isBool(ex);
  }


  @Override
  public void handle(ASTBooleanAndOpExpression ex) {
    System.out.println("Visit AndOpEx");
    this.isBool = this.basicTypesCalculator.isBool(ex);

  }


  @Override
  public void handle(ASTBooleanLiteral lit) {
    System.out.println("Visit BooleanLit");
    this.isBool = this.basicTypesCalculator.isBool(lit);
  }

  @Override
  public void handle(ASTNameExpression nameExpression) {
    System.out.println("Visit NameExpression");
    this.isBool = this.basicTypesCalculator.isBool(nameExpression);
  }

}
