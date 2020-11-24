package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCFullGenericTypesNode;
import de.monticore.types.mcfullgenerictypes._visitor.MCFullGenericTypesTraverser;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCSimpleGenericTypesNode;

public class MCFullGenericTypesFullPrettyPrinter extends MCSimpleGenericTypesFullPrettyPrinter {

  protected MCFullGenericTypesTraverser traverser;

  public MCFullGenericTypesFullPrettyPrinter(IndentPrinter printer) {
    super(printer);
    this.traverser = MCFullGenericTypesMill.traverser();

    MCFullGenericTypesPrettyPrinter fullGenericTypes = new MCFullGenericTypesPrettyPrinter(printer);
    traverser.setMCFullGenericTypesHandler(fullGenericTypes);
    traverser.addMCFullGenericTypesVisitor(fullGenericTypes);

    MCSimpleGenericTypesPrettyPrinter simpleGenericTypes = new MCSimpleGenericTypesPrettyPrinter(printer);
    traverser.setMCSimpleGenericTypesHandler(simpleGenericTypes);
    traverser.addMCSimpleGenericTypesVisitor(simpleGenericTypes);

    MCCollectionTypesPrettyPrinter collectionTypes = new MCCollectionTypesPrettyPrinter(printer);
    traverser.setMCCollectionTypesHandler(collectionTypes);
    traverser.addMCCollectionTypesVisitor(collectionTypes);

    MCBasicTypesPrettyPrinter basicTypes = new MCBasicTypesPrettyPrinter(printer);
    traverser.setMCBasicTypesHandler(basicTypes);
    traverser.addMCBasicTypesVisitor(basicTypes);

    MCBasicsPrettyPrinter basics = new MCBasicsPrettyPrinter(printer);
    traverser.addMCBasicsVisitor(basics);
  }

  @Override
  public MCFullGenericTypesTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(MCFullGenericTypesTraverser traverser) {
    this.traverser = traverser;
  }

}
