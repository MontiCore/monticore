/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.grammarfamily._symboltable;

import de.monticore.symboltable.ImportStatement;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GrammarFamilyScopesGenitor extends GrammarFamilyScopesGenitorTOP {


  /**
   * Creates the symbol table starting from the <code>rootNode</code> and
   * returns the first scope that was created.
   *
   * @param rootNode the root node
   * @return the first scope that was created
   */
  public GrammarFamilyArtifactScope createFromAST(de.monticore.grammar.grammar._ast.ASTMCGrammar rootNode) {
    Log.errorIfNull(rootNode, "0xA7FE5 Error by creating of the GrammarFamilyScopesGenitor symbol table: top ast node is null");
    List<ImportStatement> imports = new ArrayList<>();
    rootNode.getImportStatementList().stream().forEach(i -> imports.add(new ImportStatement(i.getQName(), i.isStar())));
    GrammarFamilyArtifactScope artifactScope = new GrammarFamilyArtifactScope(Optional.empty(), Names.getQualifiedName(rootNode.getPackageList()), imports);
    artifactScope.setName(rootNode.getName());
    putOnStack(artifactScope);
    rootNode.accept(getTraverser());
    return artifactScope;
  }

  /**
   * Creates the symbol table starting from the <code>rootNode</code> and
   * returns the first scope that was created.
   *
   * @param rootNode the root node
   * @return the first scope that was created
   */
  public GrammarFamilyArtifactScope createFromAST(de.monticore.cdbasis._ast.ASTCDCompilationUnit rootNode) {
    GrammarFamilyArtifactScope artifactScope = new GrammarFamilyArtifactScope(Optional.empty(),
        Names.getQualifiedName(rootNode.getCDPackageList()), new ArrayList<>());
    artifactScope.setAstNode(rootNode);
    artifactScope.setImportsList(rootNode.getMCImportStatementList().stream().map(i -> new ImportStatement(i.getQName(), i.isStar())).collect(Collectors.toList()));
    putOnStack(artifactScope);
    rootNode.accept(getTraverser());
    return artifactScope;
  }
}
