/* (c) https://github.com/MontiCore/monticore */
package de.monticore.prettyprint;

import de.monticore.literals.prettyprint.MCCommonLiteralsPrettyPrinter;
import de.monticore.umlmodifier.UMLModifierMill;
import de.monticore.umlmodifier._ast.ASTUMLModifierNode;
import de.monticore.umlmodifier._visitor.UMLModifierTraverser;

@Deprecated(forRemoval = true)
public class UMLModifierFullPrettyPrinter {

  protected UMLModifierTraverser traverser;

  protected IndentPrinter printer;

  public UMLModifierFullPrettyPrinter(IndentPrinter printer){
    this.printer = printer;
    this.traverser = UMLModifierMill.traverser();

    UMLModifierPrettyPrinter umlModifier = new UMLModifierPrettyPrinter(printer);
    traverser.add4UMLModifier(umlModifier);
    traverser.setUMLModifierHandler(umlModifier);

    MCCommonLiteralsPrettyPrinter commonLiterals = new MCCommonLiteralsPrettyPrinter(printer);
    traverser.add4MCCommonLiterals(commonLiterals);
    traverser.setMCCommonLiteralsHandler(commonLiterals);

    UMLStereotypePrettyPrinter umlStereotype = new UMLStereotypePrettyPrinter(printer);
    traverser.add4UMLStereotype(umlStereotype);
    traverser.setUMLStereotypeHandler(umlStereotype);

    traverser.add4MCBasics(new MCBasicsPrettyPrinter(printer));
  }

  public UMLModifierTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(UMLModifierTraverser traverser) {
    this.traverser = traverser;
  }

  public IndentPrinter getPrinter() {
    return printer;
  }

  public String prettyprint(ASTUMLModifierNode node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }
  
}
