/* (c) https://github.com/MontiCore/monticore */

package de.monticore.prettyprint;

import de.monticore.mcbasics._visitor.MCBasicsVisitor;

/**
 * This class is responsible for pretty-printing types of the common type system. It is implemented
 * using the Visitor pattern. The Visitor pattern traverses a tree in depth first, the visit and
 * ownVisit-methods are called when a node is traversed, the endVisit methods are called when the
 * whole subtree of a node has been traversed. The ownVisit-Methods stop the automatic traversal
 * order and allow to explictly visit subtrees by calling getVisitor().startVisit(ASTNode)
 */
public class MCBasicsPrettyPrinter implements MCBasicsVisitor {

  private MCBasicsVisitor realThis = this;

  protected IndentPrinter printer;

  /**
   * Constructor.
   *
   * @param printer the printer to write to.
   */
  public MCBasicsPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  public IndentPrinter getPrinter() {
    return printer;
  }

  @Override
  public void setRealThis(MCBasicsVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public MCBasicsVisitor getRealThis() {
    return realThis;
  }

}
