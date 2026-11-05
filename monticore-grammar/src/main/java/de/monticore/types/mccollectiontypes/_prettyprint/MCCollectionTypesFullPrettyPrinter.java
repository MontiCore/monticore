package de.monticore.types.mccollectiontypes._prettyprint;

import de.monticore.prettyprint.IndentPrinter;

public class MCCollectionTypesFullPrettyPrinter extends MCCollectionTypesFullPrettyPrinterTOP {
  public MCCollectionTypesFullPrettyPrinter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  public MCCollectionTypesFullPrettyPrinter(IndentPrinter printer) {
    super(printer);
  }

  @Override
  public String prettyprint (de.monticore.ast.ASTNode node) {
    String content = super.prettyprint(node);
    return content + " ";
  }
}
