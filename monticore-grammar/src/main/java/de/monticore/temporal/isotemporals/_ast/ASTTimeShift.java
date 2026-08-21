/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.isotemporals._ast;

public class ASTTimeShift extends ASTTimeShiftTOP {
  
  /**
   * @return a string representing the TimeShift that can be processed by the second parser.
   * Methods with this purpose are uniformly called <code>toRawString</code>.
   */
  public String toRawString() {
    StringBuilder result = new StringBuilder();
    if (isPresentSign()) {
      if (getSign() == ASTSign.PLUS) {
        result.append("+");
      } else {
        result.append("-");
      }
      result.append(getHour());
      if (isPresentMinute()) {
        result.append(":");
        result.append(getMinute());
      }
    } else {
      result.append("Z");
    }
    return result.toString();
  }
}
