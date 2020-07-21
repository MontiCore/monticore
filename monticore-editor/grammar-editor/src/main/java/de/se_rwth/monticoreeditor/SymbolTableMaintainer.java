/* (c) https://github.com/MontiCore/monticore */

package de.se_rwth.monticoreeditor;

import com.google.common.collect.ImmutableList;
import de.monticore.ast.ASTNode;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsGlobalScope;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsLanguage;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsModelLoader;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.langeditor.modelstates.ModelState;

import java.nio.file.Path;
import java.util.Optional;

final class SymbolTableMaintainer {
  
  private final Grammar_WithConceptsLanguage grammarLanguage = new Grammar_WithConceptsLanguage();
  
  private final Grammar_WithConceptsGlobalScope globalScope;
  
  SymbolTableMaintainer(ModelStatesInProject astMapper, ImmutableList<Path> modelPath) {
    Grammar_WithConceptsModelLoader modelLoader = grammarLanguage.getModelLoader();
    this.globalScope = new Grammar_WithConceptsGlobalScope(new ModelPath(modelPath), grammarLanguage);
  }
  
  void acceptModelState(ModelState modelState) {
    ASTNode rootNode = modelState.getRootNode();
    if (rootNode instanceof ASTMCGrammar) {
        ASTMCGrammar mcGrammar = (ASTMCGrammar) rootNode;
        // TODO Auf neue SymTab umstellen
//        if (!mcGrammar.isPresentSymbol2()) {
//          grammarLanguage.getSymbolTableCreator(globalScope)
//              .ifPresent(symbolTableCreator -> {
//                removeOldScope(mcGrammar);
//                Scope newScope = symbolTableCreator.createFromAST(mcGrammar);
//                setNewSuperGrammarScope(newScope);
//              });
//        }
    }
  }
  
  private void removeOldScope(ASTMCGrammar mcGrammar) {
    if (!mcGrammar.isPresentSymbol()) {
      return;
    }
    Optional<MCGrammarSymbol> s = globalScope.resolveMCGrammar(mcGrammar.getSymbol().getFullName());
    if (s.isPresent()) {
      globalScope.remove(s.get());
    }
  }

  // TODO Auf neue symtab umstellen
//  private void setNewSuperGrammarScope(Scope newScope) {
//    List<MCGrammarSymbol> existingGrammarSymbols = globalScope.getSubScopes().stream()
//        .map(scope -> Scopes.getLocalSymbolsAsCollection(scope))
//        .flatMap(Collection::stream)
//        .filter(MCGrammarSymbol.class::isInstance)
//        .map(MCGrammarSymbol.class::cast)
//        .collect(Collectors.toList());
//
//    List<MCGrammarSymbol> newGrammarSymbols = Scopes.getLocalSymbolsAsCollection(newScope).stream()
//        .filter(MCGrammarSymbol.class::isInstance)
//        .map(MCGrammarSymbol.class::cast)
//        .collect(Collectors.toList());
//
//    for (MCGrammarSymbol existingGrammarSymbol : existingGrammarSymbols) {
//      for (MCGrammarSymbol newGrammarSymbol : newGrammarSymbols) {
//        try {
//          replaceSuperGrammar(existingGrammarSymbol, newGrammarSymbol);
//        }
//        catch (Exception e) {
//          Log.error(
//              "0xA1100 Error while updating supergrammar references for " + existingGrammarSymbol.getName(),
//              e);
//        }
//      }
//    }
//  }
  
  private void replaceSuperGrammar(MCGrammarSymbol existingGrammarSymbol,
      MCGrammarSymbol newGrammarSymbol) {
    // TODO MB Check new symbol table
//    List<MCGrammarSymbol> superGrammars = new ArrayList<>(existingGrammarSymbol.getSuperGrammars());
//    boolean wasSuperGrammar = superGrammars.removeIf(superGrammar ->
//        superGrammar.getFullName().equals(newGrammarSymbol.getFullName()));
//    if (wasSuperGrammar) {
//      superGrammars.add(newGrammarSymbol);
//    }
//    existingGrammarSymbol.setSuperGrammars(superGrammars);
  }
}
