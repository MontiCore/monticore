package de.monticore.typescalculator;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.assignmentexpressionswithliterals._visitor.AssignmentExpressionsWithLiteralsVisitor;
import de.monticore.expressions.assignmentexpressionswithliterals._ast.ASTExtLiteral;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisScope;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._symboltable.MCTypeSymbol;

import java.util.Map;

public class AssignmentExpressionsWithLiteralsTypesCalculator extends AssignmentExpressionTypesCalculator implements AssignmentExpressionsWithLiteralsVisitor {

  public AssignmentExpressionsWithLiteralsTypesCalculator(){
    result=super.getResult();
    scope=super.getScope();
    literalsVisitor=super.getLiteralsVisitor();
    types=super.getTypes();
  }

  @Override
  public void endVisit(ASTExtLiteral lit){
    if(!types.containsKey(lit)) {
      ASTMCType type = literalsVisitor.calculateType(lit.getLiteral());
      MCTypeSymbol sym = new MCTypeSymbol(type.getBaseName());
      sym.setASTMCType(type);
      types.put(lit, sym);
    }
  }

  public ASTMCType getResult(){
    return super.getResult();
  }

  public ExpressionsBasisScope getScope(){
    return super.getScope();
  }

  public LiteralTypeCalculator getLiteralsVisitor(){
    return super.getLiteralsVisitor();
  }

  public Map<ASTNode, MCTypeSymbol> getTypes(){
    return super.getTypes();
  }

  public void setScope(ExpressionsBasisScope scope){
    this.scope=scope;
    super.setScope(scope);
  }

  public void setLiteralsVisitor(LiteralTypeCalculator literalsVisitor){
    this.literalsVisitor=literalsVisitor;
    super.setLiteralsVisitor(literalsVisitor);
  }
}
