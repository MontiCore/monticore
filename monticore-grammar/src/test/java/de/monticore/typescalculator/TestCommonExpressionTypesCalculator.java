package de.monticore.typescalculator;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisScope;
import de.monticore.mcbasicliterals._ast.ASTBooleanLiteral;
import de.monticore.mcbasicliterals._ast.ASTCharLiteral;
import de.monticore.mcbasicliterals._ast.ASTSignedNatLiteral;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.monticore.types.mcbasictypes._symboltable.MCTypeSymbol;
import de.monticore.typescalculator.testcommonexpressions._ast.ASTBooleanExpression;
import de.monticore.typescalculator.testcommonexpressions._ast.ASTDoubleExpression;
import de.monticore.typescalculator.testcommonexpressions._ast.ASTExtLiteral;
import de.monticore.typescalculator.testcommonexpressions._visitor.TestCommonExpressionsVisitor;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.typescalculator.testcommonexpressions._ast.ASTIntExpression;

import java.util.Map;

public class TestCommonExpressionTypesCalculator extends CommonExpressionTypesCalculator implements TestCommonExpressionsVisitor {

  private Map<ASTNode, MCTypeSymbol> types;

  private ASTMCType result;

  private ExpressionsBasisScope scope;

  private LiteralTypeCalculator literalsVisitor;

  public TestCommonExpressionTypesCalculator(){
    types=getTypes();
    result=getResult();
    scope=getScope();
    literalsVisitor=getLiteralsVisitor();
  }

  @Override
  public void endVisit(ASTIntExpression expr){
    MCTypeSymbol sym = new MCTypeSymbol("int");
    sym.setASTMCType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT));
    types.put(expr,sym);
  }

  @Override
  public void endVisit(ASTDoubleExpression expr){
    MCTypeSymbol sym = new MCTypeSymbol("double");
    sym.setASTMCType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE));
    types.put(expr,sym);
  }

  @Override
  public void endVisit(ASTBooleanExpression expr){
    MCTypeSymbol sym = new MCTypeSymbol("boolean");
    sym.setASTMCType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN));
    types.put(expr,sym);
  }

  @Override
  public void endVisit(ASTExtLiteral expr){
    ASTMCType type = literalsVisitor.calculateType(expr);
    MCTypeSymbol sym = new MCTypeSymbol(type.getBaseName());
    sym.setASTMCType(type);
    types.put(expr,sym);
  }

  public ASTMCType getResult() {
    return super.getResult();
  }

  public void setScope(ExpressionsBasisScope scope){
    this.scope=scope;
    super.setScope(scope);
  }

  public void setLiteralsVisitor(LiteralTypeCalculator literalsVisitor){
    this.literalsVisitor=literalsVisitor;
  }

  //TODO: ExtLiteral soll IntExpression, DoubleExpression und BooleanExpression ersetzen

}
