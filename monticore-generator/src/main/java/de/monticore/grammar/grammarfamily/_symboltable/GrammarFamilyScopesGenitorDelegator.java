/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.grammarfamily._symboltable;

public class GrammarFamilyScopesGenitorDelegator extends GrammarFamilyScopesGenitorDelegatorTOP {


  public GrammarFamilyScopesGenitorDelegator() {
    super();
  }

  /**
   * Creates the symbol table starting from the <code>rootNode</code> and
   * returns the first scope that was created.
   *
   * @param rootNode the root node
   * @return the first scope that was created
   */
  public IGrammarFamilyArtifactScope createFromAST(de.monticore.grammar.grammar._ast.ASTMCGrammar rootNode) {
    IGrammarFamilyArtifactScope as =  symbolTable.createFromAST(rootNode);
    if (!as.getPackageName().isEmpty()){
      globalScope.addLoadedFile(as.getPackageName() + "." + as.getName() + ".mc4");
    } else {
      globalScope.addLoadedFile(as.getName() + ".mc4");
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
  public IGrammarFamilyArtifactScope createFromAST(de.monticore.cdbasis._ast.ASTCDCompilationUnit rootNode) {
    IGrammarFamilyArtifactScope as =  symbolTable.createFromAST(rootNode);
    if (!as.getPackageName().isEmpty()){
      globalScope.addLoadedFile(as.getPackageName() + "." + as.getName());
    } else {
      globalScope.addLoadedFile(as.getName());
    }
    return as;
  }

}
