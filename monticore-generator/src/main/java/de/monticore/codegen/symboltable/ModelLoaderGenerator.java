/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.symboltable;

import de.monticore.generating.GeneratorEngine;
import de.monticore.grammar.grammar._symboltable._symboltable.MCGrammarSymbol;
import de.monticore.io.paths.IterablePath;

public interface ModelLoaderGenerator {

  void generate(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper,
                IterablePath handCodedPath, MCGrammarSymbol grammarSymbol);

}
