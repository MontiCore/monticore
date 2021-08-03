/* (c) https://github.com/MontiCore/monticore */
package de.monticore.prettyprint;

import de.monticore.umlmodifier._ast.ASTModifier;
import de.monticore.umlmodifier._ast.ASTUMLModifierNode;
import de.monticore.umlmodifier._visitor.UMLModifierHandler;
import de.monticore.umlmodifier._visitor.UMLModifierTraverser;
import de.monticore.umlmodifier._visitor.UMLModifierVisitor2;

public class UMLModifierPrettyPrinter implements UMLModifierVisitor2, UMLModifierHandler {

  protected UMLModifierTraverser traverser;

  protected IndentPrinter printer;

  public UMLModifierPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  @Override
  public void handle(ASTModifier a) {
    // print stereotypes
    if (a.isPresentStereotype()) {
      a.getStereotype().accept(getTraverser());
      getPrinter().print(" ");
    }
    if (a.isPublic()) {
      getPrinter().print("public ");
    }
    if (a.isPrivate()) {
      getPrinter().print("private ");
    }
    if (a.isProtected()) {
      getPrinter().print("protected ");
    }
    if (a.isFinal()) {
      getPrinter().print("final ");
    }
    if (a.isAbstract()) {
      getPrinter().print("abstract ");
    }
    if (a.isLocal()) {
      getPrinter().print("local ");
    }
    if (a.isDerived()) {
      getPrinter().print("derived ");
    }
    if (a.isReadonly()) {
      getPrinter().print("readonly ");
    }
    if (a.isStatic()) {
      getPrinter().print("static ");
    }
  }

  @Override
  public UMLModifierTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(UMLModifierTraverser traverser) {
    this.traverser = traverser;
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

}
