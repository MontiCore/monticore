// (c) https://github.com/MontiCore/monticore
package prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import mcnumbers.MCNumbersMill;
import mcnumbers._ast.ASTMCNumbersNode;
import mcnumbers._visitor.MCNumbersTraverser;

public class MCNumbersFullPrettyPrinter {

  private MCNumbersTraverser traverser;

  protected IndentPrinter printer;

  public MCNumbersFullPrettyPrinter(IndentPrinter printer){
    this.printer = printer;
    this.traverser = MCNumbersMill.traverser();

    MCNumbersPrettyPrinter numbers = new MCNumbersPrettyPrinter(printer);
    traverser.add4MCNumbers(numbers);
    traverser.setMCNumbersHandler(numbers);

    traverser.add4MCBasics(new MCBasicsPrettyPrinter(printer));
  }

  public MCNumbersTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(MCNumbersTraverser traverser) {
    this.traverser = traverser;
  }

  public IndentPrinter getPrinter() {
    return printer;
  }

  public void setPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  public String prettyprint(ASTMCNumbersNode node){
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return printer.getContent();
  }

}
