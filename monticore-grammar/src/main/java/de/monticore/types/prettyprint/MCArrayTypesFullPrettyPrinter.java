package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.types.mcarraytypes.MCArrayTypesMill;
import de.monticore.types.mcarraytypes._ast.ASTMCArrayTypesNode;
import de.monticore.types.mcarraytypes._visitor.MCArrayTypesTraverser;
import de.monticore.types.mccollectiontypes.MCCollectionTypesMill;
import de.monticore.types.mccollectiontypes._ast.ASTMCCollectionTypesNode;
import de.monticore.types.mccollectiontypes._visitor.MCCollectionTypesTraverser;

public class MCArrayTypesFullPrettyPrinter extends MCBasicTypesFullPrettyPrinter {

  protected MCArrayTypesTraverser traverser;

  public MCArrayTypesFullPrettyPrinter(IndentPrinter printer) {
    super(printer);

    this.traverser = MCArrayTypesMill.traverser();

    MCArrayTypesPrettyPrinter arrayTypes = new MCArrayTypesPrettyPrinter(printer);
    traverser.setMCArrayTypesHandler(arrayTypes);
    traverser.addMCArrayTypesVisitor(arrayTypes);

    MCBasicTypesPrettyPrinter basicTypes = new MCBasicTypesPrettyPrinter(printer);
    traverser.setMCBasicTypesHandler(basicTypes);
    traverser.addMCBasicTypesVisitor(basicTypes);

    MCBasicsPrettyPrinter basics = new MCBasicsPrettyPrinter(printer);
    traverser.addMCBasicsVisitor(basics);
  }

  public MCArrayTypesTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(MCArrayTypesTraverser traverser) {
    this.traverser = traverser;
  }
}
