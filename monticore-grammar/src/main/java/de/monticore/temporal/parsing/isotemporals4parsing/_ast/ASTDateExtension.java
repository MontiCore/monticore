package de.monticore.temporal.parsing.isotemporals4parsing._ast;

public class ASTDateExtension extends ASTDateExtensionTOP {
  
  public String getRawString() {
    StringBuilder result = new StringBuilder();
    if (getSign() == ASTSign4P.MINUS) {
      result.append("-");
    } else {
      result.append("+");
    }
    result.append(getLeadingDigits().getSource());
    return result.toString();
  }
}
