/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcarraytypes._ast.ASTMCArrayType;
import de.monticore.types.mcarraytypes._visitor.MCArrayTypesVisitor;

public class MCArrayTypesPrettyPrinter extends MCBasicTypesPrettyPrinter implements MCArrayTypesVisitor {
  private MCArrayTypesVisitor realThis = this;

  public MCArrayTypesPrettyPrinter(IndentPrinter printer) {
    super(printer);
  }

  public MCArrayTypesPrettyPrinter(IndentPrinter printer, MCArrayTypesPrettyPrinter realThis) {
    super(printer);
    this.realThis = realThis;
  }

  @Override
  public MCArrayTypesVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(MCArrayTypesVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public void handle(ASTMCArrayType node) {
    node.getMCType().accept(getRealThis());
    for (int i = 0; i < node.getDimensions(); i++) {
      getPrinter().print("[]");
    }
  }

}
