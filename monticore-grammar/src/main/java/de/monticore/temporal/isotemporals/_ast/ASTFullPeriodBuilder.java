package de.monticore.temporal.isotemporals._ast;

import de.monticore.temporal.isotemporals.parsing.ISOTemporals2ndParser;

import java.util.Optional;

public class ASTFullPeriodBuilder extends ASTFullPeriodBuilderTOP {
  
  public ASTFullPeriodBuilder() {
    super();
    years = Optional.empty();
    months = Optional.empty();
    days = Optional.empty();
    hours = Optional.empty();
    minutes = Optional.empty();
    seconds = Optional.empty();
    decimalDigits = Optional.empty();
  }
  
  @Override
  public ASTFullPeriod build() {
    ASTFullPeriod result = super.build();
    ISOTemporals2ndParser.doParse(result);
    return result;
  }
  
  @Override
  public ASTFullPeriod uncheckedBuild() {
    ASTFullPeriod result = super.uncheckedBuild();
    ISOTemporals2ndParser.doParse(result);
    return result;
  }
  
}
