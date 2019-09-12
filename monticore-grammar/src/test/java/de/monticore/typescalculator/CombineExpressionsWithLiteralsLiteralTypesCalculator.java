/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import de.monticore.ast.ASTNode;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.types2.DeriveSymTypeOfMCCommonLiterals;
import de.monticore.types2.SymTypeExpression;
import de.monticore.typescalculator.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsVisitor;

import java.util.Map;
import java.util.Optional;

public class CombineExpressionsWithLiteralsLiteralTypesCalculator implements CombineExpressionsWithLiteralsVisitor {

  private DeriveSymTypeOfMCCommonLiterals literalsVisitor;

  private CombineExpressionsWithLiteralsVisitor realThis;

  private LastResult lastResult;

  @Override
  public void setRealThis(CombineExpressionsWithLiteralsVisitor realThis) {
    this.realThis = realThis;
  }

  public CombineExpressionsWithLiteralsVisitor getRealThis(){
    return realThis;
  }

  @Override
  public void endVisit(ASTLiteral lit){
    Optional<SymTypeExpression> type = literalsVisitor.calculateType(lit);
    lastResult.setLastOpt(type);
  }

  public CombineExpressionsWithLiteralsLiteralTypesCalculator(){
    realThis=this;
    literalsVisitor=new DeriveSymTypeOfMCCommonLiterals();
  }

  public void setLastResult(LastResult lastResult){
    this.lastResult = lastResult;
  }
}
