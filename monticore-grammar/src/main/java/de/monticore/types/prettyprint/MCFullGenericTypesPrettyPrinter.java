/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCInnerType;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCMultipleGenericType;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCWildcardTypeArgument;
import de.monticore.types.mcfullgenerictypes._visitor.MCFullGenericTypesHandler;
import de.monticore.types.mcfullgenerictypes._visitor.MCFullGenericTypesTraverser;
import de.monticore.types.mcfullgenerictypes._visitor.MCFullGenericTypesVisitor2;

public class MCFullGenericTypesPrettyPrinter implements MCFullGenericTypesVisitor2, MCFullGenericTypesHandler {

  protected MCFullGenericTypesTraverser traverser;

  protected IndentPrinter printer;

  public MCFullGenericTypesPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  public IndentPrinter getPrinter() {
    return printer;
  }

  public void setPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  @Override
  public MCFullGenericTypesTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(MCFullGenericTypesTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void handle(ASTMCWildcardTypeArgument node) {
    getPrinter().print("?");
    if (node.isPresentUpperBound()) {
      getPrinter().print(" extends ");
      node.getUpperBound().accept(getTraverser());
    } else if (node.isPresentLowerBound()) {
      getPrinter().print(" super ");
      node.getLowerBound().accept(getTraverser());
    }
  }

  @Override
  public void handle(ASTMCInnerType innerType) {
    getPrinter().print(innerType.getName());
    if(!innerType.getMCTypeArgumentList().isEmpty()) {
      getPrinter().print("<");
      String komma = "";
      for (ASTMCTypeArgument arg : innerType.getMCTypeArgumentList()) {
        getPrinter().print(komma);
        arg.accept(getTraverser());
        komma = ",";
      }
      getPrinter().print(">");
    }

  }

  @Override
  public void handle(ASTMCMultipleGenericType node) {
    // prints first part a.b.C.E<F>
    node.getMCBasicGenericType().accept(getTraverser());

    for(ASTMCInnerType innerType : node.getMCInnerTypeList()) {
      getPrinter().print(".");
      innerType.accept(getTraverser());
    }
  }
}
