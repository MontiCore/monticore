package de.monticore.literals.prettyprint;

import de.monticore.literals.mcjavaliterals.MCJavaLiteralsMill;
import de.monticore.literals.mcjavaliterals._visitor.MCJavaLiteralsTraverser;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;

public class MCJavaLiteralsFullPrettyPrinter extends MCCommonLiteralsFullPrettyPrinter {

  private MCJavaLiteralsTraverser traverser;

  public MCJavaLiteralsFullPrettyPrinter(IndentPrinter printer) {
    super(printer);
    this.traverser = MCJavaLiteralsMill.traverser();

    MCJavaLiteralsPrettyPrinter javaLiterals = new MCJavaLiteralsPrettyPrinter(printer);
    traverser.setMCJavaLiteralsHandler(javaLiterals);
    traverser.addMCJavaLiteralsVisitor(javaLiterals);

    MCCommonLiteralsPrettyPrinter commonLiterals = new MCCommonLiteralsPrettyPrinter(printer);
    traverser.setMCCommonLiteralsHandler(commonLiterals);
    traverser.addMCCommonLiteralsVisitor(commonLiterals);

    MCBasicsPrettyPrinter basics = new MCBasicsPrettyPrinter(printer);
    traverser.addMCBasicsVisitor(basics);
  }

  public void setTraverser(MCJavaLiteralsTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public MCJavaLiteralsTraverser getTraverser() {
    return traverser;
  }
}
