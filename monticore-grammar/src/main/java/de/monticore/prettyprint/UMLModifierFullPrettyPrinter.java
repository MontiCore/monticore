package de.monticore.prettyprint;

import de.monticore.literals.prettyprint.MCCommonLiteralsPrettyPrinter;
import de.monticore.umlmodifier.UMLModifierMill;
import de.monticore.umlmodifier._ast.ASTUMLModifierNode;
import de.monticore.umlmodifier._visitor.UMLModifierTraverser;

public class UMLModifierFullPrettyPrinter {

  private UMLModifierTraverser traverser;

  protected IndentPrinter printer;

  public UMLModifierFullPrettyPrinter(IndentPrinter printer){
    this.printer = printer;
    this.traverser = UMLModifierMill.traverser();

    UMLModifierPrettyPrinter umlModifier = new UMLModifierPrettyPrinter(printer);
    traverser.addUMLModifierVisitor(umlModifier);
    traverser.setUMLModifierHandler(umlModifier);

    MCCommonLiteralsPrettyPrinter commonLiterals = new MCCommonLiteralsPrettyPrinter(printer);
    traverser.addMCCommonLiteralsVisitor(commonLiterals);
    traverser.setMCCommonLiteralsHandler(commonLiterals);

    UMLStereotypePrettyPrinter umlStereotype = new UMLStereotypePrettyPrinter(printer);
    traverser.addUMLStereotypeVisitor(umlStereotype);
    traverser.setUMLStereotypeHandler(umlStereotype);

    traverser.addMCBasicsVisitor(new MCBasicsPrettyPrinter(printer));
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
