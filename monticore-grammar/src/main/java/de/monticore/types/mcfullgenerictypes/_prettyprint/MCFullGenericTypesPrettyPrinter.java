package de.monticore.types.mcfullgenerictypes._prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCInnerType;

public class MCFullGenericTypesPrettyPrinter extends MCFullGenericTypesPrettyPrinterTOP {
  public MCFullGenericTypesPrettyPrinter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  @Override
  public void handle(ASTMCInnerType node) {
    super.handle(node);
    if (!node.isEmptyMCTypeArguments()) {
      printer.print(" ");
    }
  }
}
