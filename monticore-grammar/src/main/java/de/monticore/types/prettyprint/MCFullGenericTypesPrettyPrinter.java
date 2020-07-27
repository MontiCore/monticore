/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCArrayType;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCInnerType;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCMultipleGenericType;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCWildcardTypeArgument;
import de.monticore.types.mcfullgenerictypes._visitor.MCFullGenericTypesVisitor;

public class MCFullGenericTypesPrettyPrinter extends MCSimpleGenericTypesPrettyPrinter implements MCFullGenericTypesVisitor {
  private MCFullGenericTypesVisitor realThis = this;

  public MCFullGenericTypesPrettyPrinter(IndentPrinter printer) {
    super(printer);
  }

  public MCFullGenericTypesPrettyPrinter(IndentPrinter printer, MCFullGenericTypesVisitor realThis) {
    super(printer);
    this.realThis = realThis;
  }

  @Override
  public MCFullGenericTypesVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(MCFullGenericTypesVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public void handle(ASTMCWildcardTypeArgument node) {
    getPrinter().print("?");
    if (node.isPresentUpperBound()) {
      getPrinter().print(" extends ");
      node.getUpperBound().accept(getRealThis());
    } else if (node.isPresentLowerBound()) {
      getPrinter().print(" super ");
      node.getLowerBound().accept(getRealThis());
    }
  }

  @Override
  public void handle(ASTMCInnerType innerType) {
    getPrinter().print(innerType.getName());
    if(!innerType.getMCTypeArgumentsList().isEmpty()) {
      getPrinter().print("<");
      String komma = "";
      for (ASTMCTypeArgument arg : innerType.getMCTypeArgumentsList()) {
        getPrinter().print(komma);
        arg.accept(getRealThis());
        komma = ",";
      }
      getPrinter().print(">");
    }

  }

  @Override
  public void handle(ASTMCMultipleGenericType node) {
    // prints first part a.b.C.E<F>
    node.getMCBasicGenericType().accept(getRealThis());

    for(ASTMCInnerType innerType : node.getMCInnerTypesList()) {
      getPrinter().print(".");
      innerType.accept(getRealThis());
    }
  }

  @Override
  public void handle(ASTMCArrayType node) {
    node.getMCType().accept(getRealThis());
    for (int i = 0; i < node.getDimensions(); i++) {
      getPrinter().print("[]");
    }
  }

  public String prettyprint(ASTMCWildcardTypeArgument a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }
}
