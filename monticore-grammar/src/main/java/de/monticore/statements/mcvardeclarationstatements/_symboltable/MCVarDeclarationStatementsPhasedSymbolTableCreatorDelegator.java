package de.monticore.statements.mcvardeclarationstatements._symboltable;

public class MCVarDeclarationStatementsPhasedSymbolTableCreatorDelegator extends MCVarDeclarationStatementsPhasedSymbolTableCreatorDelegatorTOP {

  public MCVarDeclarationStatementsPhasedSymbolTableCreatorDelegator(IMCVarDeclarationStatementsGlobalScope globalScope) {
    super(globalScope);
    this.priorityList.add(new MCVarDeclarationStatementsSTCompleteTypes());
  }

  public MCVarDeclarationStatementsPhasedSymbolTableCreatorDelegator(){
    super();
    this.priorityList.add(new MCVarDeclarationStatementsSTCompleteTypes());
  }

}
