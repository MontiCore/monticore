/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import de.monticore.ast.ASTNode;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.types2.SymTypeExpression;
import de.monticore.typescalculator.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsVisitor;

import java.util.Map;

public class CombineExpressionsWithLiteralsLiteralTypesCalculator implements CombineExpressionsWithLiteralsVisitor {

  private CommonLiteralsTypesCalculator literalsVisitor;

  private Map<ASTNode, SymTypeExpression> types;

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
      SymTypeExpression type = literalsVisitor.calculateType(lit);
      types.put(lit, type);
    }
  }

  public void setTypes(Map<ASTNode, SymTypeExpression> types) {
    this.types = types;
  }

  public CombineExpressionsWithLiteralsLiteralTypesCalculator(){
    realThis=this;
    literalsVisitor=new CommonLiteralsTypesCalculator();
  }
}
