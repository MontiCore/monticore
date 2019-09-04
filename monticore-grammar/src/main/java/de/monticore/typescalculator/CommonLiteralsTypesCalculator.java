/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.literals.mccommonliterals._ast.*;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsVisitor;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.types2.DeriveSymTypeOfLiterals;
import de.monticore.types2.SymTypeOfObject;
import de.monticore.types2.SymTypeConstant;
import de.monticore.types2.SymTypeExpression;

import java.util.Map;
import java.util.Optional;

/**
 * Visitor for Derivation of SymType from Literals
 * (Function 2b)
 * i.e. for
 *    literals/MCLiteralsBasis.mc4
 */
public class CommonLiteralsTypesCalculator extends DeriveSymTypeOfLiterals implements MCCommonLiteralsVisitor {

  private SymTypeExpression result;

  private MCCommonLiteralsVisitor realThis = this;

  private Map<ASTNode, SymTypeExpression> types;

  private IExpressionsBasisScope scope;


  @Override
  public Optional<SymTypeExpression> calculateType(ASTLiteral lit) {
    lit.accept(this);
    return Optional.of(result);
  }

  public void setTypes(Map<ASTNode, SymTypeExpression> types) {
    this.types = types;
  }

  public Map<ASTNode, SymTypeExpression> getTypes() {
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
    SymTypeExpression res = new SymTypeConstant();
    res.setName("char");
    this.result=res;
    types.put(lit,res);
  }

  @Override
  public void visit(ASTBooleanLiteral lit){
    SymTypeExpression res = new SymTypeConstant();
    res.setName("boolean");
    this.result=res;
    types.put(lit,res);
  }

  @Override
  public void visit(ASTNatLiteral lit){
    SymTypeExpression res = new SymTypeConstant();
    res.setName("int");
    this.result=res;
    types.put(lit,res);
  }

  @Override
  public void visit(ASTBasicDoubleLiteral lit){
    SymTypeExpression res = new SymTypeConstant();
    res.setName("double");
    this.result=res;
    types.put(lit,res);
  }

  @Override
  public void visit(ASTBasicFloatLiteral lit){
    SymTypeExpression res = new SymTypeConstant();
    res.setName("float");
    this.result=res;
    types.put(lit,res);
  }

  @Override
  public void visit(ASTBasicLongLiteral lit){
    SymTypeExpression res = new SymTypeConstant();
    res.setName("long");
    this.result=res;
    types.put(lit,res);
  }

  @Override
  public void visit(ASTStringLiteral lit){
    SymTypeExpression res = new SymTypeOfObject();
    res.setName("String");
    this.result=res;
    types.put(lit,res);
  }


  public void setScope(IExpressionsBasisScope scope){
    this.scope=scope;
  }


}
