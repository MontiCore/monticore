package de.monticore.temporal.isotemporals._ast;

import de.monticore.temporal.isotemporals.parsing.ISOTemporals2ndParser;

public class ASTISODateTimeBuilder extends ASTISODateTimeBuilderTOP {
  
  @Override
  public ASTISODateTime build() {
    ASTISODateTime result = super.build();
    ISOTemporals2ndParser.doParse(result);
    return result;
  }
  
  @Override
  public ASTISODateTime uncheckedBuild() {
    ASTISODateTime result = super.uncheckedBuild();
    ISOTemporals2ndParser.doParse(result);
    return result;
  }
}
