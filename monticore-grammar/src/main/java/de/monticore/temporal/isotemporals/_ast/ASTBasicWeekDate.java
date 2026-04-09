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
  
  public String toRawString() {
    return getSource();
  }
}
