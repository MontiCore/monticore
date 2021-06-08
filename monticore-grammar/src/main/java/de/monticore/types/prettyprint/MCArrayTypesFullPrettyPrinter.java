/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.types.mcarraytypes.MCArrayTypesMill;
import de.monticore.types.mcarraytypes._visitor.MCArrayTypesTraverser;

public class MCArrayTypesFullPrettyPrinter extends MCBasicTypesFullPrettyPrinter {

  private MCArrayTypesTraverser traverser;

  public MCArrayTypesFullPrettyPrinter(IndentPrinter printer) {
    super(printer);
    this.traverser = MCArrayTypesMill.traverser();

    MCArrayTypesPrettyPrinter arrayTypes = new MCArrayTypesPrettyPrinter(printer);
    traverser.setMCArrayTypesHandler(arrayTypes);
    traverser.add4MCArrayTypes(arrayTypes);

    MCBasicTypesPrettyPrinter basicTypes = new MCBasicTypesPrettyPrinter(printer);
    traverser.setMCBasicTypesHandler(basicTypes);
    traverser.add4MCBasicTypes(basicTypes);

    MCBasicsPrettyPrinter basics = new MCBasicsPrettyPrinter(printer);
    traverser.add4MCBasics(basics);
  }

  public MCArrayTypesTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(MCArrayTypesTraverser traverser) {
    this.traverser = traverser;
  }
}
