/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.exptojava;/* (c) https://github.com/MontiCore/monticore */

import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

public class ExpressionBasisJavaPrinter extends ExpressionsBasisPrettyPrinter {
  
  public ExpressionBasisJavaPrinter(IndentPrinter printer) {
    super(printer);
  }
  
  public ExpressionBasisJavaPrinter() {
    super(new IndentPrinter());
  }
  
}