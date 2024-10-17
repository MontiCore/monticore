/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check.helpers;

import de.monticore.expressions.commonexpressions.CommonExpressionsMill;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symboltable.ISymbol;

/**
 * Implements {@link DefiningSymbolSetter} for <i>common expressions</i> by using traversers and handlers to
 * automatically down cast expressions to their concrete type.
 */
public class DefiningSymbolSetter4CommonExpressions implements DefiningSymbolSetter {

  protected CommonExpressionsTraverser traverser;

  DefiningSymbolHandler handler4commonAndBasicExpressions;

  public DefiningSymbolSetter4CommonExpressions() {
    this.traverser = CommonExpressionsMill.traverser();
    init();
  }

  protected void init() {
    this.handler4commonAndBasicExpressions = new DefiningSymbolHandler();
    this.traverser.setCommonExpressionsHandler(handler4commonAndBasicExpressions);
    this.traverser.setExpressionsBasisHandler(handler4commonAndBasicExpressions);
  }

  @Override
  public void setDefiningSymbol(ASTExpression expressionToLink, ISymbol symbolToLink) {
    this.handler4commonAndBasicExpressions.setExpressionToLink(expressionToLink);
    this.handler4commonAndBasicExpressions.setSymbolToLink(symbolToLink);
    expressionToLink.accept(this.traverser);
  }
}
