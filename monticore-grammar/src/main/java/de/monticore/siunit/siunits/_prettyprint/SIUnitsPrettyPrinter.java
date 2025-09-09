package de.monticore.siunit.siunits._prettyprint;

import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.siunit.siunits._ast.ASTSIUnitKindGroupWithExponent;
import de.monticore.siunit.siunits._ast.ASTSIUnitWithPrefix;

public class SIUnitsPrettyPrinter extends SIUnitsPrettyPrinterTOP {

  public SIUnitsPrettyPrinter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  //mostly removed trailing whitespaces from default pretty printer

  @Override
  public void handle(ASTSIUnitWithPrefix node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    if (node.isPresentName()) {
      getPrinter().print(node.getName());
    }
    else if (node.isPresentNonNameUnit()) {
      getPrinter().print(node.getNonNameUnit());
    }

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(de.monticore.siunit.siunits._ast.ASTSIUnitWithoutPrefix node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    if (node.isPresentName()) {
      getPrinter().print(node.getName());
    }
    else if (node.isPresentNonNameUnit()) {
      getPrinter().print(node.getNonNameUnit());
    }

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(de.monticore.siunit.siunits._ast.ASTCelsiusFahrenheit node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    getPrinter().stripTrailing();
    getPrinter().print("°");
    getPrinter().print(node.getUnit());

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }

  }

  @Override
  public void handle(de.monticore.siunit.siunits._ast.ASTSIUnitDimensionless node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    if (node.isPresentUnit()) {
      getPrinter().print(node.getUnit());
    }
    else {
      getPrinter().stripTrailing();
      getPrinter().print("°");
    }

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }

  }

  @Override
  public void handle(ASTSIUnitKindGroupWithExponent node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    for (int i = 0; i < node.sizeExponent(); i++) {
      node.getSIUnitGroupPrimitive(i).accept(getTraverser());
      getPrinter().print("^");
      node.getExponent(i).accept(getTraverser());
    }
    // optional end without exponent
    if (node.sizeExponent() != node.sizeSIUnitGroupPrimitives()) {
      node.getSIUnitGroupPrimitive(node.sizeSIUnitGroupPrimitives() - 1)
          .accept(getTraverser());
    }

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

}
