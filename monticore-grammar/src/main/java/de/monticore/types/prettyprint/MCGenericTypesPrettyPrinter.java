package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasicgenericstypes._ast.ASTMCTypeArgument;
import de.monticore.types.mccustomgenericstypes._ast.ASTMCBasicGenericsReferenceType;
import de.monticore.types.mcgenerictypes._ast.*;
import de.monticore.types.mcgenerictypes._visitor.MCGenericTypesVisitor;

public class MCGenericTypesPrettyPrinter extends MCCustomGenericsTypesPrettyPrinter implements MCGenericTypesVisitor {
  private MCGenericTypesVisitor realThis = this;

  public MCGenericTypesPrettyPrinter(IndentPrinter printer) {
    super(printer);
  }

  public MCGenericTypesPrettyPrinter(IndentPrinter printer, MCGenericTypesVisitor realThis) {
    super(printer);
    this.realThis = realThis;
  }

  @Override
  public MCGenericTypesVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(MCGenericTypesVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public void handle(ASTMCWildcardType node) {
    getPrinter().print("? ");
    if (node.isPresentUpperBound()) {
      getPrinter().print("extends ");
      node.getUpperBound().accept(getRealThis());
    } else if (node.isPresentLowerBound()) {
      getPrinter().print("super ");
      node.getLowerBound().accept(getRealThis());
    }
  }

  @Override
  public void handle(ASTMCComplexReferenceType node) {
    boolean first = true;
    for (ASTMCBasicGenericsReferenceType referenceType : node.getMCBasicGenericsReferenceTypeList()) {
      if (!first) {
        getPrinter().print(".");
      } else {
        first = false;
      }
      referenceType.accept(getRealThis());
    }
    getPrinter().print(".");
    getPrinter().print(String.join(".", node.getNameList()));
    getPrinter().print("<");
    first = true;
    for (ASTMCTypeArgument argument : node.getMCTypeArgumentList()) {
      if (!first) {
        getPrinter().print(", ");
      } else {
        first = false;
      }
      argument.accept(getRealThis());
    }
    getPrinter().print(">");

  }

  @Override
  public void handle(ASTMCArrayType node) {
    node.getMCType().accept(getRealThis());
    for (int i = 0; i < node.getDimensions(); i++) {
      getPrinter().print("[]");
    }
  }

  @Override
  public void handle(ASTMCTypeVariableDeclaration node) {
    getPrinter().print(node.getName() + " ");
    if (!node.isEmptyUpperBounds()) {
      getPrinter().print("extends ");
      boolean first = true;
      for (ASTMCComplexReferenceType type : node.getUpperBoundList()) {
        if (first) {
          first = false;
        } else {
          getPrinter().print(" & ");
        }
        type.accept(getRealThis());
      }
    }
  }

  @Override
  public void handle(ASTMCTypeParameters node) {
    getPrinter().print("<");
    boolean first = true;
    for (ASTMCTypeVariableDeclaration var : node.getMCTypeVariableDeclarationList()) {
      if (first) {
        first = false;
      } else {
        getPrinter().print(", ");
      }
      var.accept(getRealThis());
    }
    getPrinter().print(">");
  }

  public String prettyprint(ASTMCWildcardType a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTMCTypeParameters a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTMCTypeVariableDeclaration a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }


}
