/* (c) https://github.com/MontiCore/monticore */
package prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import mcnumbers._ast.ASTDecimal;
import mcnumbers._ast.ASTInteger;
import mcnumbers._ast.ASTMCNumbersNode;
import mcnumbers._visitor.MCNumbersHandler;
import mcnumbers._visitor.MCNumbersTraverser;
import mcnumbers._visitor.MCNumbersVisitor2;

@Deprecated(forRemoval = true)
public class MCNumbersPrettyPrinter implements MCNumbersVisitor2, MCNumbersHandler {

  protected MCNumbersTraverser traverser;

  private IndentPrinter printer = null;
  
  public MCNumbersPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  @Override
  public MCNumbersTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(MCNumbersTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void handle(ASTDecimal node) {
    getPrinter().print(node.getSource());
  }
  
  @Override
  public void handle(ASTInteger node) {
    if (node.isNegative()) {
      getPrinter().print("-");
    }
    getPrinter().print(node.getDecimalpart());
  }
  
  public IndentPrinter getPrinter() {
    return this.printer;
  }
  
  public String prettyprint(ASTMCNumbersNode node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }
  
}
