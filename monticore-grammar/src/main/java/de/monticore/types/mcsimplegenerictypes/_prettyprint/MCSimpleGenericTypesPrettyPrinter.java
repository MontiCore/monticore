package de.monticore.types.mcsimplegenerictypes._prettyprint;

import de.monticore.prettyprint.IndentPrinter;

public class MCSimpleGenericTypesPrettyPrinter extends MCSimpleGenericTypesPrettyPrinterTOP {
  public MCSimpleGenericTypesPrettyPrinter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }


  @Override
  public void handle (de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType node) {
    super.handle(node);
    printer.print(" ");
  }
}
