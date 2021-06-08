/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.monticore.types.mcfullgenerictypes._visitor.MCFullGenericTypesTraverser;

public class MCFullGenericTypesFullPrettyPrinter extends MCSimpleGenericTypesFullPrettyPrinter {

  private MCFullGenericTypesTraverser traverser;

  public MCFullGenericTypesFullPrettyPrinter(IndentPrinter printer) {
    super(printer);
    this.traverser = MCFullGenericTypesMill.traverser();

    MCFullGenericTypesPrettyPrinter fullGenericTypes = new MCFullGenericTypesPrettyPrinter(printer);
    traverser.setMCFullGenericTypesHandler(fullGenericTypes);
    traverser.add4MCFullGenericTypes(fullGenericTypes);

    MCSimpleGenericTypesPrettyPrinter simpleGenericTypes = new MCSimpleGenericTypesPrettyPrinter(printer);
    traverser.setMCSimpleGenericTypesHandler(simpleGenericTypes);
    traverser.add4MCSimpleGenericTypes(simpleGenericTypes);

    MCCollectionTypesPrettyPrinter collectionTypes = new MCCollectionTypesPrettyPrinter(printer);
    traverser.setMCCollectionTypesHandler(collectionTypes);
    traverser.add4MCCollectionTypes(collectionTypes);

    MCBasicTypesPrettyPrinter basicTypes = new MCBasicTypesPrettyPrinter(printer);
    traverser.setMCBasicTypesHandler(basicTypes);
    traverser.add4MCBasicTypes(basicTypes);

    MCBasicsPrettyPrinter basics = new MCBasicsPrettyPrinter(printer);
    traverser.add4MCBasics(basics);
  }

  @Override
  public MCFullGenericTypesTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(MCFullGenericTypesTraverser traverser) {
    this.traverser = traverser;
  }

}
