/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.codegen.symboltable;

import de.monticore.generating.GeneratorEngine;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.io.paths.IterablePath;

import java.util.Collection;

public interface SymbolMillGenerator {

  void generate(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper,
                IterablePath handCodedPath, MCGrammarSymbol grammarSymbol, Collection<MCProdSymbol> allSymbolDefiningRules);
}
