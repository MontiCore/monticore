/* (c)  https://github.com/MontiCore/monticore */

package de.se_rwth.monticoreeditor;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import de.monticore.ModelingLanguageFamily;
import de.monticore.ast.ASTNode;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.grammar.symboltable.MontiCoreGrammarLanguage;
import de.monticore.grammar.symboltable.MontiCoreGrammarModelLoader;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Scopes;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.langeditor.modelstates.ModelState;

final class SymbolTableMaintainer {
  
  private final MontiCoreGrammarLanguage grammarLanguage = new MontiCoreGrammarLanguage();
  
  private final GlobalScope globalScope;
  
  SymbolTableMaintainer(ModelStatesInProject astMapper, ImmutableList<Path> modelPath) {
    MontiCoreGrammarModelLoader modelLoader =
        (MontiCoreGrammarModelLoader) grammarLanguage.getModelLoader();
    modelLoader.setAstProvider(astMapper);
    ModelingLanguageFamily languageFamily = new ModelingLanguageFamily();
    languageFamily.addModelingLanguage(grammarLanguage);
    this.globalScope = new GlobalScope(new ModelPath(modelPath), languageFamily);
  }
  
  void acceptModelState(ModelState modelState) {
    ASTNode rootNode = modelState.getRootNode();
    if (rootNode instanceof ASTMCGrammar) {
        ASTMCGrammar mcGrammar = (ASTMCGrammar) rootNode;
        if (!mcGrammar.getSymbol().isPresent()) {
          grammarLanguage.getSymbolTableCreator(new ResolvingConfiguration(), globalScope)
              .ifPresent(symbolTableCreator -> {
                removeOldScope(mcGrammar);
                Scope newScope = symbolTableCreator.createFromAST(mcGrammar);
                setNewSuperGrammarScope(newScope);
              });
        }
    }
  }
  
  private void removeOldScope(ASTMCGrammar mcGrammar) {
    globalScope.getSubScopes().stream()
        .filter(scope -> Scopes.getLocalSymbolsAsCollection(scope).stream()
            .filter(MCGrammarSymbol.class::isInstance)
            .map(MCGrammarSymbol.class::cast)
            .map(MCGrammarSymbol::getName)
            .anyMatch(name -> name.endsWith(mcGrammar.getName())))
        .forEach(globalScope::removeSubScope);
  }
  
  private void setNewSuperGrammarScope(Scope newScope) {
    List<MCGrammarSymbol> existingGrammarSymbols = globalScope.getSubScopes().stream()
        .map(scope -> Scopes.getLocalSymbolsAsCollection(scope))
        .flatMap(Collection::stream)
        .filter(MCGrammarSymbol.class::isInstance)
        .map(MCGrammarSymbol.class::cast)
        .collect(Collectors.toList());
    
    List<MCGrammarSymbol> newGrammarSymbols = Scopes.getLocalSymbolsAsCollection(newScope).stream()
        .filter(MCGrammarSymbol.class::isInstance)
        .map(MCGrammarSymbol.class::cast)
        .collect(Collectors.toList());
    
    for (MCGrammarSymbol existingGrammarSymbol : existingGrammarSymbols) {
      for (MCGrammarSymbol newGrammarSymbol : newGrammarSymbols) {
        try {
          replaceSuperGrammar(existingGrammarSymbol, newGrammarSymbol);
        }
        catch (Exception e) {
          Log.error(
              "0xA1100 Error while updating supergrammar references for " + existingGrammarSymbol.getName(),
              e);
        }
      }
    }
  }
  
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
