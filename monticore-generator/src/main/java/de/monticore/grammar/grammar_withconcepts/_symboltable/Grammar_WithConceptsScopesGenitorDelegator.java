/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.grammar_withconcepts._symboltable;

public class Grammar_WithConceptsScopesGenitorDelegator extends Grammar_WithConceptsScopesGenitorDelegatorTOP {

  public Grammar_WithConceptsScopesGenitorDelegator(
      IGrammar_WithConceptsGlobalScope globalScope) {
    super(globalScope);
  }

  public Grammar_WithConceptsScopesGenitorDelegator() {
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
    Grammar_WithConceptsArtifactScope as =  symbolTable.createFromAST(rootNode);
    if (!as.getPackageName().isEmpty()){
      globalScope.addLoadedFile(as.getPackageName() + "." + as.getName());
    } else {
      globalScope.addLoadedFile(as.getName());
    }
    return as;
  }

  /**
   * Creates the symbol table starting from the <code>rootNode</code> and
   * returns the first scope that was created.
   *
   * @param rootNode the root node
   * @return the first scope that was created
   */
  public Grammar_WithConceptsArtifactScope createFromAST(de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit rootNode) {
    Grammar_WithConceptsArtifactScope as =  symbolTable.createFromAST(rootNode);
    if (!as.getPackageName().isEmpty()){
      globalScope.addLoadedFile(as.getPackageName() + "." + as.getName());
    } else {
      globalScope.addLoadedFile(as.getName());
    }
    return as;
  }

}
