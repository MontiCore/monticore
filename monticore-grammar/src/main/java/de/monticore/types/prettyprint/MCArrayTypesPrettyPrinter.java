/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcarraytypes.MCArrayTypesMill;
import de.monticore.types.mcarraytypes._ast.ASTMCArrayType;
import de.monticore.types.mcarraytypes._ast.ASTMCArrayTypesNode;
import de.monticore.types.mcarraytypes._visitor.MCArrayTypesHandler;
import de.monticore.types.mcarraytypes._visitor.MCArrayTypesTraverser;
import de.monticore.types.mcarraytypes._visitor.MCArrayTypesVisitor2;

public class MCArrayTypesPrettyPrinter implements MCArrayTypesVisitor2, MCArrayTypesHandler {

  protected MCArrayTypesTraverser traverser;

  protected IndentPrinter printer;

  public MCArrayTypesPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  public IndentPrinter getPrinter() {
    return printer;
  }

  public void setPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  @Override
  public MCArrayTypesTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(MCArrayTypesTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void handle(ASTMCArrayType node) {
    node.getMCType().accept(getTraverser());
    for (int i = 0; i < node.getDimensions(); i++) {
      getPrinter().print("[]");
    }
  }

}
