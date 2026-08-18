/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.isotemporals._ast;

public class ASTFraction extends ASTFractionTOP {
  
  /**
   * @return a string representing the Fraction that can be processed by the second parser.
   * Methods with this purpose are uniformly called <code>toRawString</code>.
   */
  public String toRawString() {
    char delimiter = isPresentPeriod()? '.' : ',';
    return delimiter + getDigits();
  }
  
}
