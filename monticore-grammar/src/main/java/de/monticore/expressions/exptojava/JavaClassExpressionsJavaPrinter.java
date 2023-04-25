/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.exptojava;/* (c) https://github.com/MontiCore/monticore */

import de.monticore.expressions.prettyprint.JavaClassExpressionsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

@Deprecated(forRemoval = true)
public class JavaClassExpressionsJavaPrinter extends JavaClassExpressionsPrettyPrinter {
  
  public JavaClassExpressionsJavaPrinter(IndentPrinter printer) {
    super(printer);
  }
 
  public JavaClassExpressionsJavaPrinter() {
    super(new IndentPrinter());
  }
  
}
