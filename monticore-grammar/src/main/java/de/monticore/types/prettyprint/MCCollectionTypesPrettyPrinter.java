/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mccollectiontypes._ast.*;
import de.monticore.types.mccollectiontypes._visitor.MCCollectionTypesVisitor;

public class MCCollectionTypesPrettyPrinter extends MCBasicTypesPrettyPrinter implements MCCollectionTypesVisitor {
  private MCCollectionTypesVisitor realThis = this;

  public MCCollectionTypesPrettyPrinter(IndentPrinter printer) {
    super(printer);
  }

  @Override
  public MCCollectionTypesVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(MCCollectionTypesVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public void handle(ASTMCListType a) {
    getPrinter().print("List<");
    a.getMCTypeArgument().accept(getRealThis());
    getPrinter().print(">");
  }

  @Override
  public void handle(ASTMCOptionalType a) {
    getPrinter().print("Optional<");
    a.getMCTypeArgument().accept(getRealThis());
    getPrinter().print(">");
  }

  @Override
  public void handle(ASTMCSetType a) {
    getPrinter().print("Set<");
    a.getMCTypeArgument().accept(getRealThis());
    getPrinter().print(">");
  }

  @Override
  public void handle(ASTMCMapType a) {
    getPrinter().print("Map<");
    a.getKey().accept(getRealThis());
    getPrinter().print(",");
    a.getValue().accept(getRealThis());
    getPrinter().print(">");
  }

  public String prettyprint(ASTMCTypeArgument a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }

}
