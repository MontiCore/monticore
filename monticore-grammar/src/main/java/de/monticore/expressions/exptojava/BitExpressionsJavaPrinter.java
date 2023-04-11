/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.exptojava;/* (c) https://github.com/MontiCore/monticore */

import de.monticore.expressions.prettyprint.BitExpressionsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

@Deprecated(forRemoval = true)
public class BitExpressionsJavaPrinter extends BitExpressionsPrettyPrinter {
  
  public BitExpressionsJavaPrinter(IndentPrinter printer) {
    super(printer);
  }
  
  public BitExpressionsJavaPrinter() {
    super(new IndentPrinter());
  }
  
}
