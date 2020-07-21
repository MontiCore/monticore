/* (c) https://github.com/MontiCore/monticore */
package de.monticore.prettyprint;

import de.monticore.umlstereotype._ast.ASTStereoValue;
import de.monticore.umlstereotype._ast.ASTStereotype;
import de.monticore.umlstereotype._ast.ASTUMLStereotypeNode;
import de.monticore.umlstereotype._visitor.UMLStereotypeVisitor;

public class UMLStereotypePrettyPrinter implements UMLStereotypeVisitor {

  private UMLStereotypeVisitor realThis = this;
  
  private IndentPrinter printer;
  
  public UMLStereotypePrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
  }
  
  @Override
  public void handle(ASTStereotype a) {
    getPrinter().print("<<");
    String sep = "";
    for (ASTStereoValue value : a.getValuesList()) {
      getPrinter().print(sep);
      value.accept(getRealThis());
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
  
  public IndentPrinter getPrinter() {
    return this.printer;
  }
  
  public String prettyprint(ASTUMLStereotypeNode node) {
    getPrinter().clearBuffer();
    node.accept(getRealThis());
    return getPrinter().getContent();
  }

  @Override
  public void setRealThis(UMLStereotypeVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public UMLStereotypeVisitor getRealThis() {
    return realThis;
  }

}
