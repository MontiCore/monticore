package de.monticore.temporal.isotemporals._ast;

import de.monticore.temporal.isotemporals.parsing.ISOTemporals2ndParser;

import java.util.Optional;

public class ASTBasicWeekDateBuilder extends ASTBasicWeekDateBuilderTOP {
  
  public ASTBasicWeekDateBuilder() {
    super();
    dayOfWeekInternal = Optional.empty();
  }
  
  @Override
  public ASTBasicWeekDate build() {
    ASTBasicWeekDate result = super.build();
    ISOTemporals2ndParser.doParse(result);
    return result;
  }
  
  @Override
  public ASTBasicWeekDate uncheckedBuild() {
    ASTBasicWeekDate result = super.uncheckedBuild();
    ISOTemporals2ndParser.doParse(result);
    return result;
  }
}
