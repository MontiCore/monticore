/*******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, 2016, MontiCore, All rights reserved.
 *  
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.se_rwth.monticoreeditor;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import de.monticore.ModelingLanguageFamily;
import de.monticore.ast.ASTNode;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.io.paths.ModelPath;
import de.monticore.languages.grammar.MCGrammarSymbol;
import de.monticore.languages.grammar.MontiCoreGrammarLanguage;
import de.monticore.languages.grammar.MontiCoreGrammarModelLoader;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolverConfiguration;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.references.FailedLoadingSymbol;
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
      try {
        ASTMCGrammar mcGrammar = (ASTMCGrammar) rootNode;
        if (!mcGrammar.getSymbol().isPresent()) {
          grammarLanguage.getSymbolTableCreator(new ResolverConfiguration(), globalScope)
              .ifPresent(symbolTableCreator -> {
                removeOldScope(mcGrammar);
                Scope newScope = symbolTableCreator.createFromAST(mcGrammar);
                setNewSuperGrammarScope(newScope);
              });
        }
      }
      catch (FailedLoadingSymbol e) {
        e.printStackTrace();
      }
    }
  }
  
  private void removeOldScope(ASTMCGrammar mcGrammar) {
    globalScope.getSubScopes().stream()
        .filter(scope -> scope.getSymbols().stream()
            .filter(MCGrammarSymbol.class::isInstance)
            .map(MCGrammarSymbol.class::cast)
            .map(MCGrammarSymbol::getName)
            .anyMatch(name -> name.endsWith(mcGrammar.getName())))
        .forEach(globalScope::removeSubScope);
  }
  
  private void setNewSuperGrammarScope(Scope newScope) {
    List<MCGrammarSymbol> existingGrammarSymbols = globalScope.getSubScopes().stream()
        .map(Scope::getSymbols)
        .flatMap(Collection::stream)
        .filter(MCGrammarSymbol.class::isInstance)
        .map(MCGrammarSymbol.class::cast)
        .collect(Collectors.toList());
    
    List<MCGrammarSymbol> newGrammarSymbols = newScope.getSymbols().stream()
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
              "Error while updating supergrammar references for " + existingGrammarSymbol.getName(),
              e);
        }
      }
    }
  }
  
  private void replaceSuperGrammar(MCGrammarSymbol existingGrammarSymbol,
      MCGrammarSymbol newGrammarSymbol) {
    List<MCGrammarSymbol> superGrammars = new ArrayList<>(existingGrammarSymbol.getSuperGrammars());
    boolean wasSuperGrammar = superGrammars.removeIf(superGrammar ->
        superGrammar.getFullName().equals(newGrammarSymbol.getFullName()));
    if (wasSuperGrammar) {
      superGrammars.add(newGrammarSymbol);
    }
    existingGrammarSymbol.setSuperGrammars(superGrammars);
  }
}
