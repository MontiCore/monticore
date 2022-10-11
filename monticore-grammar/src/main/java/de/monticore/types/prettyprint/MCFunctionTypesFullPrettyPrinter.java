/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.types.mcfunctiontypes.MCFunctionTypesMill;
import de.monticore.types.mcfunctiontypes._visitor.MCFunctionTypesTraverser;

public class MCFunctionTypesFullPrettyPrinter extends MCBasicTypesFullPrettyPrinter {

  protected MCFunctionTypesTraverser traverser;

  public MCFunctionTypesFullPrettyPrinter(IndentPrinter printer) {
    super(printer);
    traverser = MCFunctionTypesMill.traverser();

    MCFunctionTypesPrettyPrinter functionTypes = new MCFunctionTypesPrettyPrinter(printer);
    traverser.setMCFunctionTypesHandler(functionTypes);
    traverser.add4MCFunctionTypes(functionTypes);

    MCBasicTypesPrettyPrinter basicTypes = new MCBasicTypesPrettyPrinter(printer);
    traverser.setMCBasicTypesHandler(basicTypes);
    traverser.add4MCBasicTypes(basicTypes);

    MCBasicsPrettyPrinter basics = new MCBasicsPrettyPrinter(printer);
    traverser.add4MCBasics(basics);
  }

  public void setTraverser(MCFunctionTypesTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public MCFunctionTypesTraverser getTraverser() {
    return traverser;
  }
}
