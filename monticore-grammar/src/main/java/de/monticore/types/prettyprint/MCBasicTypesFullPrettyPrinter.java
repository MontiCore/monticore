/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCBasicTypesNode;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesTraverser;

public class MCBasicTypesFullPrettyPrinter {

  private MCBasicTypesTraverser traverser;

  protected IndentPrinter printer;

  public MCBasicTypesFullPrettyPrinter(IndentPrinter printer){
    this.printer = printer;
    this.traverser = MCBasicTypesMill.traverser();

    MCBasicTypesPrettyPrinter basicTypes = new MCBasicTypesPrettyPrinter(printer);
    traverser.setMCBasicTypesHandler(basicTypes);
    traverser.add4MCBasicTypes(basicTypes);

    MCBasicsPrettyPrinter basics = new MCBasicsPrettyPrinter(printer);
    traverser.add4MCBasics(basics);
  }

  public IndentPrinter getPrinter() {
    return printer;
  }

  public void setPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  public MCBasicTypesTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(MCBasicTypesTraverser traverser) {
    this.traverser = traverser;
  }

  /**
   * This method prettyprints a given node from type grammar.
   *
   * @param a A node from type grammar.
   * @return String representation.
   */
  public String prettyprint(ASTMCBasicTypesNode a) {
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent();
  }

}
