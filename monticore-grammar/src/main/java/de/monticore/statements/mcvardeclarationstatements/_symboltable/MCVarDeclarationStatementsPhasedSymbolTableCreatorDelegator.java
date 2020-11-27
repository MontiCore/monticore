package de.monticore.statements.mcvardeclarationstatements._symboltable;

import de.monticore.statements.mcvardeclarationstatements.MCVarDeclarationStatementsMill;
import de.monticore.statements.mcvardeclarationstatements._visitor.MCVarDeclarationStatementsTraverser;

public class MCVarDeclarationStatementsPhasedSymbolTableCreatorDelegator extends MCVarDeclarationStatementsPhasedSymbolTableCreatorDelegatorTOP {

  public MCVarDeclarationStatementsPhasedSymbolTableCreatorDelegator(IMCVarDeclarationStatementsGlobalScope globalScope) {
    super(globalScope);
    MCVarDeclarationStatementsTraverser traverser = MCVarDeclarationStatementsMill.traverser();
    traverser.addMCVarDeclarationStatementsVisitor(new MCVarDeclarationStatementsSTCompleteTypes());
    this.priorityList.add(traverser);
  }

  public MCVarDeclarationStatementsPhasedSymbolTableCreatorDelegator(){
    super();
    MCVarDeclarationStatementsTraverser traverser = MCVarDeclarationStatementsMill.traverser();
    traverser.addMCVarDeclarationStatementsVisitor(new MCVarDeclarationStatementsSTCompleteTypes());
    this.priorityList.add(traverser);
  }

}
