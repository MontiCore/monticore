/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.grammar_withconcepts._symboltable;

public class Grammar_WithConceptsSymbolTableCreatorDelegator extends Grammar_WithConceptsSymbolTableCreatorDelegatorTOP {
  public Grammar_WithConceptsSymbolTableCreatorDelegator(
      Grammar_WithConceptsGlobalScope globalScope) {
    super(globalScope);
  }
  
  public Grammar_WithConceptsArtifactScope createFromAST(de.monticore.grammar.grammar._ast.ASTMCGrammar rootNode) {
    Grammar_WithConceptsArtifactScope as =  symbolTable.createFromAST(rootNode);
    if (!as.getPackageName().isEmpty()){
      globalScope.cache(as.getPackageName() + "." + as.getName());
    } else {
      globalScope.cache(as.getName());
    }
    return as;
  }
}
