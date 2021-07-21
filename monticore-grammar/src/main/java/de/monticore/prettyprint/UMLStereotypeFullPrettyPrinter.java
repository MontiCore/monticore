/* (c) https://github.com/MontiCore/monticore */
package de.monticore.prettyprint;

import de.monticore.literals.prettyprint.MCCommonLiteralsPrettyPrinter;
import de.monticore.umlstereotype.UMLStereotypeMill;
import de.monticore.umlstereotype._ast.ASTUMLStereotypeNode;
import de.monticore.umlstereotype._visitor.UMLStereotypeTraverser;

public class UMLStereotypeFullPrettyPrinter {

  protected UMLStereotypeTraverser traverser;

  protected IndentPrinter printer;

  public UMLStereotypeFullPrettyPrinter(IndentPrinter printer){
    this.printer = printer;
    this.traverser = UMLStereotypeMill.traverser();

    UMLStereotypePrettyPrinter umlStereotype = new UMLStereotypePrettyPrinter(printer);
    traverser.add4UMLStereotype(umlStereotype);
    traverser.setUMLStereotypeHandler(umlStereotype);

    MCCommonLiteralsPrettyPrinter commonLiterals = new MCCommonLiteralsPrettyPrinter(printer);
    traverser.add4MCCommonLiterals(commonLiterals);
    traverser.setMCCommonLiteralsHandler(commonLiterals);

    traverser.add4MCBasics(new MCBasicsPrettyPrinter(printer));
  }

  public UMLStereotypeTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(UMLStereotypeTraverser traverser) {
    this.traverser = traverser;
  }

  public IndentPrinter getPrinter() {
    return printer;
  }

  public String prettyprint(ASTUMLStereotypeNode node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }
  
  
}
