/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.isotemporals._ast;

import de.monticore.temporal.isotemporals.ISOTemporalsMill;
import de.monticore.temporal.isotemporals.parsing.ISOTemporals2ndParser;
import de.monticore.temporal.parsing.isotemporals4parsing.ISOTemporals4ParsingMill;

public class ASTExtendedWeekDateBuilder extends ASTExtendedWeekDateBuilderTOP {
  
  @Override
  public ASTExtendedWeekDate build() {
    ASTExtendedWeekDate result = super.build();
    ISOTemporals4ParsingMill.init();
    ISOTemporals2ndParser.doParse(result);
    ISOTemporalsMill.init();
    return result;
  }
  
  @Override
  public ASTExtendedWeekDate uncheckedBuild() {
    ASTExtendedWeekDate result = super.uncheckedBuild();
    ISOTemporals4ParsingMill.init();
    ISOTemporals2ndParser.doParse(result);
    ISOTemporalsMill.init();
    return result;
  }
  
}
