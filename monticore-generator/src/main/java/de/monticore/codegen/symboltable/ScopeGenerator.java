/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.symboltable;

import java.util.Collection;

import de.monticore.generating.GeneratorEngine;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.io.paths.IterablePath;

public interface ScopeGenerator {
  
  void generate(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper,
      IterablePath handCodedPath, String languageName, Collection<ProdSymbol> allSymbolDefiningRules,
                Collection<ProdSymbol> allSymbolDefiningRulesWithSuperGrammar);
}
