package de.monticore.types.mccollectiontypes._prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mccollectiontypes._ast.ASTMCListType;
import de.monticore.types.mccollectiontypes._ast.ASTMCMapType;
import de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType;
import de.monticore.types.mccollectiontypes._ast.ASTMCSetType;

public class MCCollectionTypesPrettyPrinter extends MCCollectionTypesPrettyPrinterTOP {

  public MCCollectionTypesPrettyPrinter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  @Override
  public void handle(ASTMCListType node) {
    super.handle(node);
    getPrinter().print(" ");
  }

  @Override
  public void handle(ASTMCOptionalType node) {
    super.handle(node);
    getPrinter().print(" ");
  }

  @Override
  public void handle(ASTMCMapType node) {
    super.handle(node);
    getPrinter().print(" ");
  }

  @Override
  public void handle(ASTMCSetType node) {
    super.handle(node);
    getPrinter().print(" ");
  }
}
