package de.monticore.temporal.isotemporals._ast;

import de.monticore.temporal.isotemporals.parsing.ISOTemporals2ndParser;

import java.util.Optional;

public class ASTISOTimeBuilder extends ASTISOTimeBuilderTOP {

  public ASTISOTimeBuilder() {
    super();
    minute = Optional.empty();
    second = Optional.empty();
    decimalDigits = Optional.empty();
    timeShift = Optional.empty();
  }
  
  @Override
  public ASTISOTime build() {
    ASTISOTime result = super.build();
    ISOTemporals2ndParser.doParse(result);
    return result;
  }
  
  @Override
  public ASTISOTime uncheckedBuild() {
    ASTISOTime result = super.uncheckedBuild();
    ISOTemporals2ndParser.doParse(result);
    return result;
  }
}
