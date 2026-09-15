/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.isotemporals._ast;

public class ASTFullPeriod extends ASTFullPeriodTOP {
  
  /**
   * @return a string representing the FullPeriod that can be processed by the second parser.
   * Methods with this purpose are uniformly called <code>toRawString</code>.
   */
  public String toRawString() {
    StringBuilder result = new StringBuilder();
    result.append(getPre().getSource());
    if (isPresentFraction()) {
      result.append(getFraction().toRawString());
      result.append(getPost().getSource());
    }
    return result.toString();
  }
  
}
