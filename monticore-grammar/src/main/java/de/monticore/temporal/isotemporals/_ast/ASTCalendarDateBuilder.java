/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.isotemporals._ast;

import de.monticore.temporal.isotemporals.parsing.ISOTemporals2ndParser;

import java.util.Optional;

public class ASTCalendarDateBuilder extends ASTCalendarDateBuilderTOP {
  
  public ASTCalendarDateBuilder() {
    super();
    century = Optional.empty();
    decade = Optional.empty();
    year = Optional.empty();
    month = Optional.empty();
    day = Optional.empty();
  }
  
  @Override
  public ASTCalendarDate build() {
    ASTCalendarDate result = super.build();
    ISOTemporals2ndParser.doParse(result);
    return result;
  }
  
  @Override
  public ASTCalendarDate uncheckedBuild() {
    ASTCalendarDate result = super.uncheckedBuild();
    ISOTemporals2ndParser.doParse(result);
    return result;
  }
}
