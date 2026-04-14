/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.isotemporals._ast;

import de.monticore.temporal.isotemporals.ISOTemporalsMill;
import de.monticore.temporal.isotemporals.parsing.ISOTemporals2ndParser;
import de.monticore.temporal.parsing.isotemporals4parsing.ISOTemporals4ParsingMill;

import java.util.Optional;

public class ASTBasicWeekDateBuilder extends ASTBasicWeekDateBuilderTOP {
  
  public ASTBasicWeekDateBuilder() {
    super();
    dayOfWeekInternal = Optional.empty();
  }
  
  @Override
  public ASTBasicWeekDate build() {
    ASTBasicWeekDate result = super.build();
    ISOTemporals4ParsingMill.init();
    ISOTemporals2ndParser.doParse(result);
    ISOTemporalsMill.init();
    return result;
  }
  
  @Override
  public ASTBasicWeekDate uncheckedBuild() {
    ASTBasicWeekDate result = super.uncheckedBuild();
    ISOTemporals4ParsingMill.init();
    ISOTemporals2ndParser.doParse(result);
    ISOTemporalsMill.init();
    return result;
  }
}
