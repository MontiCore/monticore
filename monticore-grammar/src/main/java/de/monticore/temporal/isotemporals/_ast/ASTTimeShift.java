package de.monticore.temporal.isotemporals._ast;

public class ASTTimeShift extends ASTTimeShiftTOP {
  
  public String toRawString() {
    StringBuilder result = new StringBuilder();
    if (isPresentSign()) {
      if (getSign() == ASTSign.PLUS) {
        result.append("+");
      } else {
        result.append("-");
      }
      result.append(getPre());
      if (isPresentPost()) {
        result.append(":");
        result.append(getPost());
      }
    } else {
      result.append("Z");
    }
    return result.toString();
  }
}
