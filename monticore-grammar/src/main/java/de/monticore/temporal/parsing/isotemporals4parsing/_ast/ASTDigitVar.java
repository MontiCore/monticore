package de.monticore.temporal.parsing.isotemporals4parsing._ast;

public class ASTDigitVar extends ASTDigitVarTOP {
  
  public String getSource() {
    return String.join("", getDigitList());
  }
}
