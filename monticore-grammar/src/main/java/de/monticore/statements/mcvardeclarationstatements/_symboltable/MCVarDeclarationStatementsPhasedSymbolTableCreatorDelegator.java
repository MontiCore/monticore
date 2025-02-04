/*(c) https://github.com/MontiCore/monticore*/
package de.monticore.statements.mcvardeclarationstatements._symboltable;

import de.monticore.statements.mcvardeclarationstatements.MCVarDeclarationStatementsMill;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTLocalVariableDeclarationStatement;
import de.monticore.statements.mcvardeclarationstatements._visitor.MCVarDeclarationStatementsTraverser;

import java.util.ArrayList;
import java.util.List;

public class MCVarDeclarationStatementsPhasedSymbolTableCreatorDelegator {

  protected IMCVarDeclarationStatementsGlobalScope globalScope;

  protected MCVarDeclarationStatementsScopesGenitorDelegator scopesGenitorDelegator;

  protected List<MCVarDeclarationStatementsTraverser> priorityList;

  public MCVarDeclarationStatementsPhasedSymbolTableCreatorDelegator(){
    this.globalScope = MCVarDeclarationStatementsMill.globalScope();
    this.scopesGenitorDelegator = MCVarDeclarationStatementsMill.scopesGenitorDelegator();
    this.priorityList = new ArrayList<>();
    MCVarDeclarationStatementsTraverser traverser = MCVarDeclarationStatementsMill.traverser();
    traverser.add4MCVarDeclarationStatements(new MCVarDeclarationStatementsSTCompleteTypes());
    this.priorityList.add(traverser);
  }

  public IMCVarDeclarationStatementsArtifactScope createFromAST(ASTLocalVariableDeclarationStatement rootNode){
    IMCVarDeclarationStatementsArtifactScope as = scopesGenitorDelegator.createFromAST(rootNode);
    this.priorityList.forEach(rootNode::accept);
    return as;
  }

}
