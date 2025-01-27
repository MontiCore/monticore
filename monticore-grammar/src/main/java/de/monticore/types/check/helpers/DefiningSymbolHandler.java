/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check.helpers;

import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsHandler;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisHandler;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.symboltable.ISymbol;

/**
 * Provides a handlers for CommonExpressions and ExpressionBasis that implement part of the logic of
 * {@link DefiningSymbolSetter}. Compose this handler with a traverser and potentially handlers for other languages to
 * get a full implementation. The traverser/handling pattern is used to downcast expressions to their real types.
 * Thereby we can easily set {@link ASTNameExpression#setDefiningSymbol(ISymbol)} or similar methods while maintaining
 * flexibility for language composition.
 * @deprecated not required in TypeCheck3.
 */
@Deprecated
public class DefiningSymbolHandler implements ExpressionsBasisHandler, CommonExpressionsHandler {

  protected CommonExpressionsTraverser traverser;

  protected ASTExpression expressionToLink;
  protected ISymbol symbolToLink;

  @Override
  public CommonExpressionsTraverser getTraverser() {
    return this.traverser;
  }

  @Override
  public void setTraverser(CommonExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  /** @param traverser must be of type {@link CommonExpressionsTraverser} */
  @Override
  public void setTraverser(ExpressionsBasisTraverser traverser) {
    if(traverser instanceof CommonExpressionsTraverser) {
      this.traverser = (CommonExpressionsTraverser) traverser;
    } else {
      throw new UnsupportedOperationException("Traverser for " + this.getClass().getName()
        + " must be a CommonExpressionsTraverser");
    }
  }

  public ASTExpression getExpressionToLink() {
    return expressionToLink;
  }

  public void setExpressionToLink(ASTExpression expressionToLink) {
    this.expressionToLink = expressionToLink;
  }

  public ISymbol getSymbolToLink() {
    return symbolToLink;
  }

  public void setSymbolToLink(ISymbol symbolToLink) {
    this.symbolToLink = symbolToLink;
  }

  @Override
  public void handle(ASTNameExpression nameExpr) {
    if(nameExpr == this.expressionToLink) {
      nameExpr.setDefiningSymbol(this.symbolToLink);
    }
  }

  @Override
  public void handle(ASTFieldAccessExpression fieldAccessExpr) {
    if (fieldAccessExpr == this.expressionToLink) {
      fieldAccessExpr.setDefiningSymbol(this.symbolToLink);
    }
  }

  @Override
  public void handle(ASTCallExpression callExpr) {
    if (callExpr == this.expressionToLink) {
      callExpr.setDefiningSymbol(this.symbolToLink);
    }
  }
}
