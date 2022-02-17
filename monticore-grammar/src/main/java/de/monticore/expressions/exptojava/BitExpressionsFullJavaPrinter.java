/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.exptojava;/* (c) https://github.com/MontiCore/monticore */

import de.monticore.expressions.bitexpressions.BitExpressionsMill;
import de.monticore.expressions.bitexpressions._visitor.BitExpressionsTraverser;
import de.monticore.expressions.prettyprint.BitExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;

public class BitExpressionsFullJavaPrinter extends ExpressionsBasisFullJavaPrinter {
  
  protected BitExpressionsTraverser traverser;
  
  @Override
  public BitExpressionsTraverser getTraverser() {
    return traverser;
  }
  
  public void setTraverser(BitExpressionsTraverser traverser) {
    this.traverser = traverser;
  }
  
  public BitExpressionsFullJavaPrinter(IndentPrinter printer) {
    
    super(printer);
    this.traverser = BitExpressionsMill.traverser();
  
    ExpressionsBasisJavaPrinter basisExpression = new ExpressionsBasisJavaPrinter(printer);
    traverser.setExpressionsBasisHandler(basisExpression);
    traverser.add4ExpressionsBasis(basisExpression);
    BitExpressionsJavaPrinter bitExpressions = new BitExpressionsJavaPrinter(printer);
    traverser.setBitExpressionsHandler(bitExpressions);
    traverser.add4BitExpressions(bitExpressions);
    
  }
  
}
