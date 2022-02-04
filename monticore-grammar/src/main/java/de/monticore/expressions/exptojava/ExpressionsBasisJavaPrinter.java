/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.exptojava;/* (c) https://github.com/MontiCore/monticore */

import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

public class ExpressionsBasisJavaPrinter extends ExpressionsBasisPrettyPrinter {
  
  public ExpressionsBasisJavaPrinter(IndentPrinter printer) {
    super(printer);
  }
  
  public ExpressionsBasisJavaPrinter() {
    super(new IndentPrinter());
  }
  
}