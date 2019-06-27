package de.monticore.typescalculator;

import de.monticore.ast.ASTNode;
import de.monticore.typescalculator.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsVisitor;
import de.monticore.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._symboltable.MCTypeSymbol;

import java.util.Map;

public class CombineExpressionsWithLiteralsLiteralTypesCalculator implements CombineExpressionsWithLiteralsVisitor {

  private CommonLiteralsTypesCalculator literalsVisitor;

  private Map<ASTNode, MCTypeSymbol> types;

  private CombineExpressionsWithLiteralsVisitor realThis;

  @Override
  public void setRealThis(CombineExpressionsWithLiteralsVisitor realThis) {
    this.realThis = realThis;
  }

  public CombineExpressionsWithLiteralsVisitor getRealThis(){
    return realThis;
  }

  @Override
  public void endVisit(ASTLiteral lit){
    if(!types.containsKey(lit)) {
      ASTMCType type = literalsVisitor.calculateType(lit);
      MCTypeSymbol sym = new MCTypeSymbol(type.getBaseName());
      sym.setASTMCType(type);
      types.put(lit, sym);
    }
  }

  public void setTypes(Map<ASTNode, MCTypeSymbol> types) {
    this.types = types;
  }

  public CombineExpressionsWithLiteralsLiteralTypesCalculator(){
    realThis=this;
    literalsVisitor=new CommonLiteralsTypesCalculator();
  }
}
