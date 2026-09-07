/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.isotemporals._ast;

public class ASTISODateTime extends ASTISODateTimeTOP {
  
  /**
   * @return a string representing the ISODateTime that can be processed by the second parser.
   * Methods with this purpose are uniformly called <code>toRawString</code>.
   */
  public String toRawString() {
    return getSource();
  }
}
