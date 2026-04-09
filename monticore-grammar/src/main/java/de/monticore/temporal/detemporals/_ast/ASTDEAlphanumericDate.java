package de.monticore.temporal.detemporals._ast;

import static de.monticore.literals.MCLiteralsDecoder.decodeNat;

public class ASTDEAlphanumericDate extends ASTDEAlphanumericDateTOP {
  
  @Override
  public int getYear() {
    return decodeNat(getYearSource());
  }
  
  @Override
  public boolean isPresentMonth() {
    return true;
  }
  
  @Override
  public int getMonth() {
    switch (getMonthSource().getMonth()) { // Index of the Month, if ordered alphabetically
      case 1: // April
        return 4;
      case 2: // August
        return 8;
      case 3: // December
        return 12;
      case 4: // February
        return 2;
      case 5: // January
        return 1;
      case 6: // July
        return 7;
      case 7: // June
        return 6;
      case 8: // March
        return 3;
      case 9: // May
        return 5;
      case 10: // November
        return 11;
      case 11: // October
        return 10;
      case 12: // September
        return 9;
    }
    throw new IllegalStateException("0x35ac3 Invalid month index");
  }
  
  @Override
  public boolean isPresentDay() {
    return isPresentDaySource();
  }
  
  @Override
  public int getDay() {
    return decodeNat(getDaySource());
  }
  
}
