/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.isotemporals._ast;

public class ASTFraction extends ASTFractionTOP {
  
  public String toRawString() {
    char delimiter = isPresentPeriod()? '.' : ',';
    return delimiter + getDigits();
  }
  
}
