package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.types.mcsimplegenerictypes.MCSimpleGenericTypesMill;
import de.monticore.types.mcsimplegenerictypes._visitor.MCSimpleGenericTypesTraverser;

public class MCSimpleGenericTypesFullPrettyPrinter extends MCCollectionTypesFullPrettyPrinter {

  private MCSimpleGenericTypesTraverser traverser;

  public MCSimpleGenericTypesFullPrettyPrinter(IndentPrinter printer) {
    super(printer);
    traverser = MCSimpleGenericTypesMill.traverser();

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

  public void setTraverser(MCSimpleGenericTypesTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public MCSimpleGenericTypesTraverser getTraverser() {
    return traverser;
  }
}
