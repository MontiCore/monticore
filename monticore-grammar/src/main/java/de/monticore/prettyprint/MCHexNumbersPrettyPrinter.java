/* (c) https://github.com/MontiCore/monticore */
package de.monticore.prettyprint;

import de.monticore.mchexnumbers._ast.ASTHexInteger;
import de.monticore.mchexnumbers._ast.ASTHexadecimal;
import de.monticore.mchexnumbers._ast.ASTMCHexNumbersNode;
import de.monticore.mchexnumbers._visitor.MCHexNumbersVisitor;

public class MCHexNumbersPrettyPrinter implements MCHexNumbersVisitor {
  
  private IndentPrinter printer = null;
  
  public MCHexNumbersPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
  }
  
  @Override
  public void handle(ASTHexadecimal node) {
    getPrinter().print(node.getSource());
  }
  
  @Override
  public void handle(ASTHexInteger node) {
    if (node.isNegative()) {
      getPrinter().print("-");
    }
    getPrinter().print(node.getHexadecimalpart());
  }
  
  public IndentPrinter getPrinter() {
    return this.printer;
  }
  
  public String prettyprint(ASTMCHexNumbersNode node) {
    getPrinter().clearBuffer();
    node.accept(getRealThis());
    return getPrinter().getContent();
  }
}
