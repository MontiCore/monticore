/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.isotemporals._ast;

public class ASTBasicWeekDate extends ASTBasicWeekDateTOP {
  
  @Override
  public boolean isPresentDayOfWeek() {
    return isPresentDayOfWeekInternal();
  }
  
  @Override
  public int getDayOfWeek() {
    return getDayOfWeekInternal();
  }
  
  /**
   * @return a string representing the DayOfWeek that can be processed by the second parser.
   * Methods with this purpose are uniformly called <code>toRawString</code>.
   */
  public String toRawString() {
    return getSource();
  }
}
