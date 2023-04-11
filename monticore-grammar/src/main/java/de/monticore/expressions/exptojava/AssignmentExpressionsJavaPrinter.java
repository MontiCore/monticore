/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.exptojava;

import de.monticore.expressions.prettyprint.AssignmentExpressionsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

@Deprecated(forRemoval = true)
public class AssignmentExpressionsJavaPrinter extends AssignmentExpressionsPrettyPrinter {
  
  public AssignmentExpressionsJavaPrinter(IndentPrinter printer) {
    super(printer);
    this.printer = printer;
  }
  
  public AssignmentExpressionsJavaPrinter() {
    super(new IndentPrinter());
  }
  
}
