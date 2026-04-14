/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.isotemporals._ast;

import de.monticore.temporal.isotemporals.ISOTemporalsMill;
import de.monticore.temporal.isotemporals.parsing.ISOTemporals2ndParser;
import de.monticore.temporal.parsing.isotemporals4parsing.ISOTemporals4ParsingMill;

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
    ISOTemporals4ParsingMill.init();
    ISOTemporals2ndParser.doParse(result);
    ISOTemporalsMill.init();
    return result;
  }
  
  @Override
  public ASTISOTime uncheckedBuild() {
    ASTISOTime result = super.uncheckedBuild();
    ISOTemporals4ParsingMill.init();
    ISOTemporals2ndParser.doParse(result);
    ISOTemporalsMill.init();
    return result;
  }
}
