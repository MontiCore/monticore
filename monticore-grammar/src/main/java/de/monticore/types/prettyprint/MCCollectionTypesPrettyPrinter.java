/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mccollectiontypes._ast.*;
import de.monticore.types.mccollectiontypes._visitor.MCCollectionTypesHandler;
import de.monticore.types.mccollectiontypes._visitor.MCCollectionTypesTraverser;
import de.monticore.types.mccollectiontypes._visitor.MCCollectionTypesVisitor2;

public class MCCollectionTypesPrettyPrinter implements MCCollectionTypesVisitor2, MCCollectionTypesHandler {

  protected IndentPrinter printer;

  protected MCCollectionTypesTraverser traverser;

  public MCCollectionTypesPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  public MCCollectionTypesTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(MCCollectionTypesTraverser traverser) {
    this.traverser = traverser;
  }

  public IndentPrinter getPrinter() {
    return printer;
  }

  public void setPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  @Override
  public void handle(ASTMCListType a) {
    getPrinter().print("List<");
    a.getMCTypeArgument().accept(getTraverser());
    getPrinter().print(">");
  }

  @Override
  public void handle(ASTMCOptionalType a) {
    getPrinter().print("Optional<");
    a.getMCTypeArgument().accept(getTraverser());
    getPrinter().print(">");
  }

  @Override
  public void handle(ASTMCSetType a) {
    getPrinter().print("Set<");
    a.getMCTypeArgument().accept(getTraverser());
    getPrinter().print(">");
  }

  @Override
  public void handle(ASTMCMapType a) {
    getPrinter().print("Map<");
    a.getKey().accept(getTraverser());
    getPrinter().print(",");
    a.getValue().accept(getTraverser());
    getPrinter().print(">");
  }

}
