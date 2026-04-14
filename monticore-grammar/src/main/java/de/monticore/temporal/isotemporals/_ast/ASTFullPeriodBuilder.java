/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.isotemporals._ast;

import de.monticore.temporal.isotemporals.ISOTemporalsMill;
import de.monticore.temporal.isotemporals.parsing.ISOTemporals2ndParser;
import de.monticore.temporal.parsing.isotemporals4parsing.ISOTemporals4ParsingMill;

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
    ISOTemporals4ParsingMill.init();
    ISOTemporals2ndParser.doParse(result);
    ISOTemporalsMill.init();
    return result;
  }
  
  @Override
  public ASTFullPeriod uncheckedBuild() {
    ASTFullPeriod result = super.uncheckedBuild();
    ISOTemporals4ParsingMill.init();
    ISOTemporals2ndParser.doParse(result);
    ISOTemporalsMill.init();
    return result;
  }
  
}
