package de.monticore.statements.mccommonstatements._symboltable;

public class MCCommonStatementsPhasedSymbolTableCreatorDelegator extends MCCommonStatementsPhasedSymbolTableCreatorDelegatorTOP {

  public MCCommonStatementsPhasedSymbolTableCreatorDelegator(IMCCommonStatementsGlobalScope globalScope) {
    super(globalScope);
    this.priorityList.add(new MCCommonStatementsSTCompleteTypes());
  }

}
