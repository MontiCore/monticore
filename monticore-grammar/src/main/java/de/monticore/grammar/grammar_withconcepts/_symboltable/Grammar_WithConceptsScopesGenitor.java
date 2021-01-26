/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.grammar_withconcepts._symboltable;

import de.monticore.symboltable.ImportStatement;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

public class Grammar_WithConceptsScopesGenitor extends Grammar_WithConceptsScopesGenitorTOP {

  public Grammar_WithConceptsScopesGenitor(IGrammar_WithConceptsScope enclosingScope) {
    super(enclosingScope);
  }

  public Grammar_WithConceptsScopesGenitor(Deque<? extends IGrammar_WithConceptsScope> scopeStack) {
    super(scopeStack);
  }

  public Grammar_WithConceptsScopesGenitor() {
    super();
  }

  /**
   * Creates the symbol table starting from the <code>rootNode</code> and
   * returns the first scope that was created.
   *
   * @param rootNode the root node
   * @return the first scope that was created
   */
  public Grammar_WithConceptsArtifactScope createFromAST(de.monticore.grammar.grammar._ast.ASTMCGrammar rootNode) {
    Log.errorIfNull(rootNode, "0xA7FE4 Error by creating of the Grammar_WithConceptsScopesGenitor symbol table: top ast node is null");
    List<ImportStatement> imports = new ArrayList<>();
    rootNode.getImportStatementList().stream().forEach(i -> imports.add(new ImportStatement(i.getQName(), i.isStar())));
    Grammar_WithConceptsArtifactScope artifactScope = new Grammar_WithConceptsArtifactScope(Optional.empty(), Names.getQualifiedName(rootNode.getPackageList()), imports);
    artifactScope.setName(rootNode.getName());
    putOnStack(artifactScope);
    rootNode.accept(getTraverser());
    return artifactScope;
  }


}
