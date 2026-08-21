/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.isotemporals._ast;

import static de.monticore.literals.MCLiteralsDecoder.decodeNat;

public class ASTExtendedWeekDate extends ASTExtendedWeekDateTOP {
  
  @Override
  public int getYear() {
    int unsignedYear = decodeNat(getYearSource());
    if (isPresentSign() && getSign() == ASTSign.MINUS) {
      return -unsignedYear;
    } else {
      return unsignedYear;
    }
  }
  
  @Override
  public int getWeek() {
    return decodeNat(getWeekSource().getSource().substring(1));
  }
  
  @Override
  public boolean isPresentDayOfWeek() {
    return isPresentDayOfWeekSource();
  }
  
  @Override
  public int getDayOfWeek() {
    return decodeNat(getDayOfWeekSource());
  }
  
}
