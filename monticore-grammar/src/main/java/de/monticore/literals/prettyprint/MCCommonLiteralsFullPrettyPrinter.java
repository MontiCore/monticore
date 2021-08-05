/* (c) https://github.com/MontiCore/monticore */
package de.monticore.literals.prettyprint;

import de.monticore.literals.mccommonliterals.MCCommonLiteralsMill;
import de.monticore.literals.mccommonliterals._ast.ASTMCCommonLiteralsNode;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsTraverser;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;

public class MCCommonLiteralsFullPrettyPrinter {

  protected MCCommonLiteralsTraverser traverser;

  protected IndentPrinter printer;

  public MCCommonLiteralsFullPrettyPrinter(IndentPrinter printer){
    this.printer = printer;
    traverser = MCCommonLiteralsMill.traverser();

    MCCommonLiteralsPrettyPrinter commonLiterals = new MCCommonLiteralsPrettyPrinter(printer);
    traverser.setMCCommonLiteralsHandler(commonLiterals);
    traverser.add4MCCommonLiterals(commonLiterals);

    MCBasicsPrettyPrinter basics = new MCBasicsPrettyPrinter(printer);
    traverser.add4MCBasics(basics);
  }

  public MCCommonLiteralsTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(MCCommonLiteralsTraverser traverser) {
    this.traverser = traverser;
  }

  public IndentPrinter getPrinter() {
    return printer;
  }

  public void setPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  public String prettyprint(ASTMCCommonLiteralsNode node){
    node.accept(getTraverser());
    return printer.getContent();
  }
}
