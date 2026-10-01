/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.grammar_withconcepts._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.symboltable.ImportStatement;
import de.se_rwth.commons.Names;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Grammar_WithConceptsScopesGenitor extends Grammar_WithConceptsScopesGenitorTOP {

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
    Preconditions.checkNotNull(rootNode, "0xA7FE4 Error by creating of the Grammar_WithConceptsScopesGenitor symbol table: top ast node is null");
    List<ImportStatement> imports = new ArrayList<>();
    rootNode.getImportStatementList().forEach(i -> imports.add(new ImportStatement(i.getQName(), i.isStar())));
    Grammar_WithConceptsArtifactScope artifactScope = new Grammar_WithConceptsArtifactScope(Optional.empty(), Names.constructQualifiedName(rootNode.getPackageList()), imports);
    artifactScope.setName(rootNode.getName());
    putOnStack(artifactScope);
    rootNode.accept(getTraverser());
    return artifactScope;
  }


}
