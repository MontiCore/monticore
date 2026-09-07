/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.isotemporals._ast;

import de.monticore.temporal.isotemporals.parsing.ISOTemporals2ndParser;

public class ASTOrdinalDateBuilder extends ASTOrdinalDateBuilderTOP {
  
  @Override
  public ASTOrdinalDate build() {
    ASTOrdinalDate result = super.build();
    ISOTemporals2ndParser.doParse(result);
    return result;
  }
  
  @Override
  public ASTOrdinalDate uncheckedBuild() {
    ASTOrdinalDate result = super.uncheckedBuild();
    ISOTemporals2ndParser.doParse(result);
    return result;
  }
  
}
