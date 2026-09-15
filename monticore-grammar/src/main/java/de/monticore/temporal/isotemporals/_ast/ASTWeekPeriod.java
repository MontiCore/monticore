/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.isotemporals._ast;

public class ASTWeekPeriod extends ASTWeekPeriodTOP {
  
  /**
   * @return a string representing the WeekPeriod that can be processed by the second parser.
   * Methods with this purpose are uniformly called <code>toRawString</code>.
   */
  public String toRawString() {
    return getSource();
  }
  
}
