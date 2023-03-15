/* (c) https://github.com/MontiCore/monticore */
package de.monticore.simplecd._symboltable;

import de.monticore.simplecd._ast.ASTCDDefinition;

/**
 * Extends the generated genitor to set the name of the created artifact scope to the name of the
 * class diagram.
 */
public class SimpleCDScopesGenitor extends SimpleCDScopesGenitorTOP {

  public SimpleCDScopesGenitor() {
    super();
  }

  @Override
  public void visit(ASTCDDefinition node) {
    final ISimpleCDScope artifactScope = scopeStack.peekLast();
    assert artifactScope != null;
    artifactScope.setName(node.getName());
    super.visit(node);
  }
}
