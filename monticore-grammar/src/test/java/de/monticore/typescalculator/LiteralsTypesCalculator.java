package de.monticore.typescalculator;

import de.monticore.ast.ASTNode;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._symboltable.MCTypeSymbol;
import de.monticore.typescalculator.combineexpressionswithliterals._ast.ASTExtLiteral;
import de.monticore.typescalculator.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsVisitor;

import java.util.Map;

public class LiteralsTypesCalculator implements CombineExpressionsWithLiteralsVisitor {

  private ASTMCType result;

  private BasicLiteralsTypeCalculator literalsVisitor;

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
  public void endVisit(ASTExtLiteral lit){
    if(!types.containsKey(lit)) {
      ASTMCType type = literalsVisitor.calculateType(lit.getLiteral());
      MCTypeSymbol sym = new MCTypeSymbol(type.getBaseName());
      sym.setASTMCType(type);
      types.put(lit, sym);
    }
  }

  public void setTypes(Map<ASTNode, MCTypeSymbol> types) {
    this.types = types;
  }

  public ASTMCType getResult() {
    return result;
  }

  public LiteralsTypesCalculator(){
    realThis=this;
    literalsVisitor=new BasicLiteralsTypeCalculator();
  }
}
