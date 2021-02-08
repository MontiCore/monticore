// (c) https://github.com/MontiCore/monticore
package de.monticore.statements.mccommonstatements._symboltable;

import de.monticore.statements.mccommonstatements.MCCommonStatementsMill;
import de.monticore.statements.mccommonstatements._ast.ASTMCJavaBlock;
import de.monticore.statements.mccommonstatements._visitor.MCCommonStatementsTraverser;

import java.util.ArrayList;
import java.util.List;

public class MCCommonStatementsPhasedSymbolTableCreatorDelegator {

  protected IMCCommonStatementsGlobalScope globalScope;

  protected MCCommonStatementsScopesGenitorDelegator scopesGenitorDelegator;

  protected List<MCCommonStatementsTraverser> priorityList;

  public MCCommonStatementsPhasedSymbolTableCreatorDelegator(IMCCommonStatementsGlobalScope globalScope) {
    this.globalScope = globalScope;
    this.scopesGenitorDelegator = new MCCommonStatementsScopesGenitorDelegator(globalScope);
    this.priorityList = new ArrayList<>();
    MCCommonStatementsTraverser traverser = MCCommonStatementsMill.traverser();
    traverser.add4MCCommonStatements(new MCCommonStatementsSTCompleteTypes());
    this.priorityList.add(traverser);
  }

  public MCCommonStatementsPhasedSymbolTableCreatorDelegator(){
    this(MCCommonStatementsMill.globalScope());
  }

  public IMCCommonStatementsArtifactScope createFromAST(ASTMCJavaBlock rootNode){
    IMCCommonStatementsArtifactScope as = scopesGenitorDelegator.createFromAST(rootNode);
    this.priorityList.forEach(rootNode::accept);
    return as;
  }

}
