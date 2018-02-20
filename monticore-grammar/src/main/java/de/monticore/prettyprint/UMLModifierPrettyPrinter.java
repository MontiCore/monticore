/* (c) https://github.com/MontiCore/monticore */
package de.monticore.prettyprint;

import de.monticore.umlmodifier._ast.ASTModifier;
import de.monticore.umlmodifier._ast.ASTUMLModifierNode;
import de.monticore.umlmodifier._visitor.UMLModifierVisitor;

public class UMLModifierPrettyPrinter implements UMLModifierVisitor {
  
  private IndentPrinter printer = null;
  
  public UMLModifierPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
  }
  
  @Override
  public void handle(ASTModifier a) {
    // print stereotypes
    if (a.isPresentStereotype()) {
      a.getStereotype().accept(getRealThis());
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
  
  public IndentPrinter getPrinter() {
    return this.printer;
  }
  
  public String prettyprint(ASTUMLModifierNode node) {
    getPrinter().clearBuffer();
    node.accept(getRealThis());
    return getPrinter().getContent();
  }
  
}
