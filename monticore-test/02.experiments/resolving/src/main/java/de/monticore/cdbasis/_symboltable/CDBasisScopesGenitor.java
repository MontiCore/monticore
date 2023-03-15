/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis._symboltable;

import de.monticore.cdbasis._ast.ASTCDDefinition;

/**
 * Extends the generated genitor to set the name of the created artifact scope to the name of the
 * class diagram.
 */
public class CDBasisScopesGenitor extends CDBasisScopesGenitorTOP {

  public CDBasisScopesGenitor() {
    super();
  }

  @Override
  public void visit(ASTCDDefinition node) {
    final ICDBasisScope artifactScope = scopeStack.peekLast();
    assert artifactScope != null;
    artifactScope.setName(node.getName());
    super.visit(node);
  }
}
