package prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import mchexnumbers.MCHexNumbersMill;
import mchexnumbers._ast.ASTMCHexNumbersNode;
import mchexnumbers._visitor.MCHexNumbersTraverser;

public class MCHexNumbersFullPrettyPrinter extends MCNumbersFullPrettyPrinter {

  private MCHexNumbersTraverser traverser;

  protected IndentPrinter printer;

  public MCHexNumbersFullPrettyPrinter(IndentPrinter printer){
    super(printer);
    this.traverser = MCHexNumbersMill.traverser();

    MCHexNumbersPrettyPrinter hexNumbers = new MCHexNumbersPrettyPrinter(printer);
    traverser.addMCHexNumbersVisitor(hexNumbers);
    traverser.setMCHexNumbersHandler(hexNumbers);

    MCNumbersPrettyPrinter numbers = new MCNumbersPrettyPrinter(printer);
    traverser.addMCNumbersVisitor(numbers);
    traverser.setMCNumbersHandler(numbers);

    traverser.addMCBasicsVisitor(new MCBasicsPrettyPrinter(printer));
  }

  public MCHexNumbersTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(MCHexNumbersTraverser traverser) {
    this.traverser = traverser;
  }
}
