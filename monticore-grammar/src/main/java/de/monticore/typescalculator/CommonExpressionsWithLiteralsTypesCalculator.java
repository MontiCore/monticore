package de.monticore.typescalculator;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.commonexpressionswithliterals._ast.ASTExtLiteral;
import de.monticore.expressions.commonexpressionswithliterals._visitor.CommonExpressionsWithLiteralsVisitor;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisScope;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._symboltable.MCTypeSymbol;

import java.util.Map;

public class CommonExpressionsWithLiteralsTypesCalculator extends CommonExpressionTypesCalculator implements CommonExpressionsWithLiteralsVisitor {

  public CommonExpressionsWithLiteralsTypesCalculator(){
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

}
