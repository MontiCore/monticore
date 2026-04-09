package de.monticore.temporal.isotemporals._ast;

import de.monticore.temporal.isotemporals.parsing.ISOTemporals2ndParser;

public class ASTExtendedWeekDateBuilder extends ASTExtendedWeekDateBuilderTOP {
  
  @Override
  public ASTExtendedWeekDate build() {
    ASTExtendedWeekDate result = super.build();
    ISOTemporals2ndParser.doParse(result);
    return result;
  }
  
  @Override
  public ASTExtendedWeekDate uncheckedBuild() {
    ASTExtendedWeekDate result = super.uncheckedBuild();
    ISOTemporals2ndParser.doParse(result);
    return result;
  }
  
}
