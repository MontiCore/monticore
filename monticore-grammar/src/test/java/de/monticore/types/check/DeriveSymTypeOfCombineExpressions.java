/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsHandler;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsTraverser;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsVisitor2;

/**
 * @deprecated is empty and does not do anything
 */
@Deprecated
public class DeriveSymTypeOfCombineExpressions extends AbstractDeriveFromExpression implements CombineExpressionsWithLiteralsVisitor2, CombineExpressionsWithLiteralsHandler {

  protected CombineExpressionsWithLiteralsTraverser traverser;

  @Override
  public CombineExpressionsWithLiteralsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(CombineExpressionsWithLiteralsTraverser traverser) {
    this.traverser = traverser;
  }

  public DeriveSymTypeOfCombineExpressions(FullSynthesizeFromCombineExpressionsWithLiterals synthesizer){
  }

}
