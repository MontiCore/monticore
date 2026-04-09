package de.monticore.temporal.isotemporals._ast;

import de.monticore.temporal.isotemporals.parsing.ISOTemporals2ndParser;

public class ASTWeekPeriodBuilder extends ASTWeekPeriodBuilderTOP {
  
  @Override
  public ASTWeekPeriod build() {
    ASTWeekPeriod result = super.build();
    ISOTemporals2ndParser.doParse(result);
    return result;
  }
  
  @Override
  public ASTWeekPeriod uncheckedBuild() {
    ASTWeekPeriod result = super.uncheckedBuild();
    ISOTemporals2ndParser.doParse(result);
    return result;
  }
}
