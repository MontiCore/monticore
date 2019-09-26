/* (c) https://github.com/MontiCore/monticore */
package prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import mchexnumbers._ast.ASTHexInteger;
import mchexnumbers._ast.ASTHexadecimal;
import mchexnumbers._ast.ASTMCHexNumbersNode;
import mchexnumbers._visitor.MCHexNumbersVisitor;

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
