/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.assignmentexpressions._ast.*;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsVisitor2;
import de.monticore.expressions.bitexpressions._ast.*;
import de.monticore.expressions.bitexpressions._visitor.BitExpressionsVisitor2;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsScope;
import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTArguments;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.expressions.javaclassexpressions._ast.*;
import de.monticore.expressions.javaclassexpressions._visitor.JavaClassExpressionsVisitor2;
import de.monticore.expressions.setexpressions._ast.ASTIntersectionExpressionInfix;
import de.monticore.expressions.setexpressions._ast.ASTIsInExpression;
import de.monticore.expressions.setexpressions._ast.ASTSetInExpression;
import de.monticore.expressions.setexpressions._ast.ASTUnionExpressionInfix;
import de.monticore.expressions.setexpressions._visitor.SetExpressionsVisitor2;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor2;

public class FlatExpressionScopeSetter implements AssignmentExpressionsVisitor2, CommonExpressionsVisitor2,
    JavaClassExpressionsVisitor2, SetExpressionsVisitor2, BitExpressionsVisitor2, ExpressionsBasisVisitor2, MCBasicTypesVisitor2 {

  private ICombineExpressionsWithLiteralsScope scope;

  public FlatExpressionScopeSetter(ICombineExpressionsWithLiteralsScope scope){
    this.scope = scope;
  }

  @Override
  public void visit(IScope scope){}

  @Override
  public void endVisit(IScope scope){}

  @Override
  public void visit(ASTNode node){}

  @Override
  public void endVisit(ASTNode node){}

  @Override
  public void visit(ISymbol symbol){}

  @Override
  public void endVisit(ISymbol symbol){}

  /*************************************************ASSIGNMENT EXPRESSIONS****************************************************/

  @Override
  public void visit(ASTAssignmentExpression expr){
    expr.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTMinusPrefixExpression expr){
    expr.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTPlusPrefixExpression expr){
    expr.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTDecPrefixExpression expr){
    expr.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTDecSuffixExpression expr){
    expr.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTIncPrefixExpression expr){
    expr.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTIncSuffixExpression expr){
    expr.setEnclosingScope(scope);
  }

  /*************************************************COMMON EXPRESSIONS****************************************************/

  @Override
  public void visit(ASTGreaterEqualExpression expr) {
    expr.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTLessEqualExpression expr){
    expr.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTGreaterThanExpression expr){
    expr.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTLessThanExpression expr){
    expr.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTPlusExpression expr){
    expr.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTMinusExpression expr){
    expr.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTMultExpression expr){
    expr.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTDivideExpression expr){
    expr.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTModuloExpression expr){
    expr.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTEqualsExpression expr){
    expr.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTNotEqualsExpression expr){
    expr.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTFieldAccessExpression expr){
    expr.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTCallExpression expr){
    expr.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTLogicalNotExpression expr){
    expr.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTBooleanAndOpExpression expr){
    expr.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTBooleanOrOpExpression expr){
    expr.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTBooleanNotExpression expr){
    expr.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTBracketExpression expr){
    expr.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTConditionalExpression expr){
    expr.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTArguments expr){
    expr.setEnclosingScope(scope);
  }

  /*************************************************BIT EXPRESSIONS****************************************************/

  @Override
  public void visit(ASTLogicalRightShiftExpression expr) {
    expr.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTRightShiftExpression expr) {
    expr.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTLeftShiftExpression expr) {
    expr.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTBinaryOrOpExpression expr){
    expr.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTBinaryAndExpression expr){
    expr.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTBinaryXorExpression expr){
    expr.setEnclosingScope(scope);
  }

  /*************************************************SET EXPRESSIONS****************************************************/

  @Override
  public void visit(ASTIsInExpression expr){
    expr.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTSetInExpression expr){
    expr.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTUnionExpressionInfix expr){
    expr.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTIntersectionExpressionInfix expr){
    expr.setEnclosingScope(scope);
  }

  /*************************************************EXPRESSIONS BASIS****************************************************/

  @Override
  public void visit(ASTLiteralExpression expr){
    expr.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTNameExpression expr){
    expr.setEnclosingScope(scope);
  }

  /*************************************************JAVA CLASS EXPRESSIONS****************************************************/

  @Override
  public void visit(ASTPrimarySuperExpression expr){
    expr.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTPrimaryThisExpression expr){
    expr.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTSuperExpression expr){
    expr.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTThisExpression expr){
    expr.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTArrayExpression expr){
    expr.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTInstanceofExpression expr){
    expr.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTTypeCastExpression expr){
    expr.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTPrimaryGenericInvocationExpression expr){
    expr.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTGenericInvocationExpression expr){
    expr.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTClassExpression expr){
    expr.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTMCQualifiedType type){
    type.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTMCQualifiedName name) {
    name.setEnclosingScope(scope);
  }

  @Override
  public void visit(ASTMCReturnType type){
    type.setEnclosingScope(scope);
  }

}
