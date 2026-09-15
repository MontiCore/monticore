/* (c) https://github.com/MontiCore/monticore */
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
    return switch (getMonthSource().getMonth()) {
      case ASTConstantsDETemporals.JANUARY -> 1;
      case ASTConstantsDETemporals.FEBRUARY -> 2;
      case ASTConstantsDETemporals.MARCH -> 3;
      case ASTConstantsDETemporals.APRIL -> 4;
      case ASTConstantsDETemporals.MAY -> 5;
      case ASTConstantsDETemporals.JUNE -> 6;
      case ASTConstantsDETemporals.JULY -> 7;
      case ASTConstantsDETemporals.AUGUST -> 8;
      case ASTConstantsDETemporals.SEPTEMBER -> 9;
      case ASTConstantsDETemporals.OCTOBER -> 10;
      case ASTConstantsDETemporals.NOVEMBER -> 11;
      case ASTConstantsDETemporals.DECEMBER -> 12;
      default -> throw new IllegalStateException("0x35ac3 Invalid month index");
    };
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
