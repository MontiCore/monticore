package prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import stringliterals.StringLiteralsMill;
import stringliterals._ast.ASTStringLiteralsNode;
import stringliterals._visitor.StringLiteralsTraverser;

public class StringLiteralsFullPrettyPrinter {

  private StringLiteralsTraverser traverser;

  protected IndentPrinter printer;

  public StringLiteralsFullPrettyPrinter(IndentPrinter printer){
    this.printer = printer;
    this.traverser = StringLiteralsMill.traverser();

    StringLiteralsPrettyPrinter stringLiterals = new StringLiteralsPrettyPrinter(printer);
    traverser.addStringLiteralsVisitor(stringLiterals);
    traverser.setStringLiteralsHandler(stringLiterals);

    traverser.addMCBasicsVisitor(new MCBasicsPrettyPrinter(printer));
  }

  public StringLiteralsTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(StringLiteralsTraverser traverser) {
    this.traverser = traverser;
  }

  public IndentPrinter getPrinter() {
    return printer;
  }

  public void setPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  public String prettyprint(ASTStringLiteralsNode node){
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return printer.getContent();
  }
}
