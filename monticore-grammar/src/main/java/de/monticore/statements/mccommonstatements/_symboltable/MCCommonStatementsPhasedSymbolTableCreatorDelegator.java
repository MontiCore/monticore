package de.monticore.statements.mccommonstatements._symboltable;

import de.monticore.statements.mccommonstatements.MCCommonStatementsMill;
import de.monticore.statements.mccommonstatements._visitor.MCCommonStatementsTraverser;

public class MCCommonStatementsPhasedSymbolTableCreatorDelegator extends MCCommonStatementsPhasedSymbolTableCreatorDelegatorTOP {

  public MCCommonStatementsPhasedSymbolTableCreatorDelegator(IMCCommonStatementsGlobalScope globalScope) {
    super(globalScope);
    MCCommonStatementsTraverser traverser = MCCommonStatementsMill.traverser();
    traverser.add4MCCommonStatements(new MCCommonStatementsSTCompleteTypes());
    this.priorityList.add(traverser);
  }

  public MCCommonStatementsPhasedSymbolTableCreatorDelegator(){
    super();
    MCCommonStatementsTraverser traverser = MCCommonStatementsMill.traverser();
    traverser.add4MCCommonStatements(new MCCommonStatementsSTCompleteTypes());
    this.priorityList.add(traverser);
  }

}
