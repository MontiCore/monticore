/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.detemporals._ast;

import static de.monticore.literals.MCLiteralsDecoder.decodeNat;

public class ASTDENumericDate extends ASTDENumericDateTOP {
  
  @Override
  public int getYear() {
    return decodeNat(getYearSource());
  }
  
  @Override
  public boolean isPresentMonth() {
    return isPresentMonthSource();
  }
  
  @Override
  public int getMonth() {
    return decodeNat(getMonthSource());
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
