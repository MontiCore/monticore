/* (c) https://github.com/MontiCore/monticore */
package de.monticore.umlmodifier._prettyprint;

import de.monticore.prettyprint.IndentPrinter;

public class UMLModifierPrettyPrinter extends UMLModifierPrettyPrinterTOP {

  protected boolean useShortForms = false;

  public UMLModifierPrettyPrinter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  public boolean isUseShortForms() {
    return useShortForms;
  }

  public void setUseShortForms(boolean useShortForms) {
    this.useShortForms = useShortForms;
  }

  @Override
  public void handle(de.monticore.umlmodifier._ast.ASTModifier node) {
    // hand-written due to the possible short forms

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    if (node.isPresentStereotype()) {
      node.getStereotype().accept(getTraverser());
    }

    if (node.isPublic()) {
      getPrinter().print(this.isUseShortForms() ? "+ " : "public ");
    }

    if (node.isPrivate()) {
      getPrinter().print(this.isUseShortForms() ? "- " : "private ");
    }

    if (node.isProtected()) { 
      getPrinter().print(this.isUseShortForms() ? "# " : "protected ");
    }

    if (node.isFinal()) { 
      getPrinter().print("final ");
    }

    if (node.isAbstract()) { 
      getPrinter().print("abstract ");
    }

    if (node.isLocal()) { 
      getPrinter().print("local ");
    }

    if (node.isDerived()) {
      getPrinter().print(this.isUseShortForms() ? "/ " : "derived ");
    }

    if (node.isReadonly()) {
      getPrinter().print(this.isUseShortForms() ? "? " : "readonly ");
    }

    if (node.isStatic()) {
      getPrinter().print("static ");
    }

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }

  }
}
