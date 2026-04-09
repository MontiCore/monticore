package de.monticore.temporal.isotemporals._ast;

public class ASTFullPeriod extends ASTFullPeriodTOP {
  
  public String toRawString() {
    StringBuilder result = new StringBuilder();
    result.append(getPre().getSource());
    if (isPresentFraction()) {
      result.append(getFraction().toRawString());
      result.append(getPost().getSource());
    }
    return result.toString();
  }
  
}
