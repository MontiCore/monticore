/*(c) https://github.com/MontiCore/monticore*/
package de.monticore.literals.prettyprint;

import de.monticore.literals.mcjavaliterals.MCJavaLiteralsMill;
import de.monticore.literals.mcjavaliterals._visitor.MCJavaLiteralsTraverser;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;

@Deprecated(forRemoval = true)
public class MCJavaLiteralsFullPrettyPrinter extends MCCommonLiteralsFullPrettyPrinter {

  protected MCJavaLiteralsTraverser traverser;

  public MCJavaLiteralsFullPrettyPrinter(IndentPrinter printer) {
    super(printer);
    this.traverser = MCJavaLiteralsMill.traverser();

    MCJavaLiteralsPrettyPrinter javaLiterals = new MCJavaLiteralsPrettyPrinter(printer);
    traverser.setMCJavaLiteralsHandler(javaLiterals);
    traverser.add4MCJavaLiterals(javaLiterals);

    MCCommonLiteralsPrettyPrinter commonLiterals = new MCCommonLiteralsPrettyPrinter(printer);
    traverser.setMCCommonLiteralsHandler(commonLiterals);
    traverser.add4MCCommonLiterals(commonLiterals);

    MCBasicsPrettyPrinter basics = new MCBasicsPrettyPrinter(printer);
    traverser.add4MCBasics(basics);
  }

  public void setTraverser(MCJavaLiteralsTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public MCJavaLiteralsTraverser getTraverser() {
    return traverser;
  }
}
