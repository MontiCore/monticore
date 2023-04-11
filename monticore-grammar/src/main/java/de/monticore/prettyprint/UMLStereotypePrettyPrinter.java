/* (c) https://github.com/MontiCore/monticore */
package de.monticore.prettyprint;

import de.monticore.umlstereotype._ast.ASTStereoValue;
import de.monticore.umlstereotype._ast.ASTStereotype;
import de.monticore.umlstereotype._ast.ASTUMLStereotypeNode;
import de.monticore.umlstereotype._visitor.UMLStereotypeHandler;
import de.monticore.umlstereotype._visitor.UMLStereotypeTraverser;
import de.monticore.umlstereotype._visitor.UMLStereotypeVisitor2;

@Deprecated(forRemoval = true)
public class UMLStereotypePrettyPrinter implements UMLStereotypeVisitor2, UMLStereotypeHandler {

  protected UMLStereotypeTraverser traverser;
  
  protected IndentPrinter printer;
  
  public UMLStereotypePrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
  }
  
  @Override
  public void handle(ASTStereotype a) {
    getPrinter().print("<<");
    String sep = "";
    for (ASTStereoValue value : a.getValuesList()) {
      getPrinter().print(sep);
      value.accept(getTraverser());
      sep = ", ";
    }
    getPrinter().print(">>");
  }
  
  @Override
  public void handle(ASTStereoValue a) {
    getPrinter().print(a.getName());
    if (a.isPresentText()) {
      getPrinter().print("=" + "\"" + a.getText().getSource() + "\"");
    }
  }

  @Override
  public UMLStereotypeTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(UMLStereotypeTraverser traverser) {
    this.traverser = traverser;
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

}
