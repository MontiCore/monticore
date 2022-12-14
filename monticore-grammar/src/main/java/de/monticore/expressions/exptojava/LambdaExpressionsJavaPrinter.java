/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.exptojava;/* (c) https://github.com/MontiCore/monticore */

import de.monticore.expressions.prettyprint.LambdaExpressionsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

public class LambdaExpressionsJavaPrinter extends LambdaExpressionsPrettyPrinter {

  public LambdaExpressionsJavaPrinter(IndentPrinter printer) {
    super(printer);
  }

  public LambdaExpressionsJavaPrinter() {
    super(new IndentPrinter());
  }
  
}
