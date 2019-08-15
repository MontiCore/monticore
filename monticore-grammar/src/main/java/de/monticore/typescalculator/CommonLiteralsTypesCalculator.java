/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.literals.mccommonliterals._ast.*;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsVisitor;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;

import java.util.Map;

public class CommonLiteralsTypesCalculator extends LiteralsBasisTypesCalculator implements MCCommonLiteralsVisitor {

  private TypeExpression result;

  private MCCommonLiteralsVisitor realThis = this;

  private Map<ASTNode, TypeExpression> types;

  private IExpressionsBasisScope scope;


  @Override
  public TypeExpression calculateType(ASTLiteral lit) {
    lit.accept(this);
    return result;
  }

  public void setTypes(Map<ASTNode, TypeExpression> types) {
    this.types = types;
  }

  public Map<ASTNode,TypeExpression> getTypes() {
    return types;
  }

  @Override
  public MCCommonLiteralsVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(MCCommonLiteralsVisitor realThis){
    this.realThis=realThis;
  }

  @Override
  public void visit(ASTNode node) {

  }

  @Override
  public void endVisit(ASTNode node) {

  }

  @Override
  public void visit(ASTCharLiteral lit){
    TypeExpression res = new TypeExpression();
    res.setName("char");
    this.result=res;
    types.put(lit,res);
  }

  @Override
  public void visit(ASTBooleanLiteral lit){
    TypeExpression res = new TypeExpression();
    res.setName("boolean");
    this.result=res;
    types.put(lit,res);
  }

  @Override
  public void visit(ASTNatLiteral lit){
    TypeExpression res = new TypeExpression();
    res.setName("int");
    this.result=res;
    types.put(lit,res);
  }

  @Override
  public void visit(ASTBasicDoubleLiteral lit){
    TypeExpression res = new TypeExpression();
    res.setName("double");
    this.result=res;
    types.put(lit,res);
  }

  @Override
  public void visit(ASTBasicFloatLiteral lit){
    TypeExpression res = new TypeExpression();
    res.setName("float");
    this.result=res;
    types.put(lit,res);
  }

  @Override
  public void visit(ASTBasicLongLiteral lit){
    TypeExpression res = new TypeExpression();
    res.setName("long");
    this.result=res;
    types.put(lit,res);
  }

  @Override
  public void visit(ASTStringLiteral lit){
    TypeExpression res = new TypeExpression();
    res.setName("String");
    this.result=res;
    types.put(lit,res);
  }


  public void setScope(IExpressionsBasisScope scope){
    this.scope=scope;
  }


}
