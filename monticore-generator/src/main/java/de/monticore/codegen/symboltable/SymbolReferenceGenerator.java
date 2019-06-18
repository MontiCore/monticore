/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.symboltable;

import de.monticore.generating.GeneratorEngine;
import de.monticore.grammar.grammar._symboltable._symboltable.MCProdSymbol;
import de.monticore.io.paths.IterablePath;

public interface SymbolReferenceGenerator {

  void generate(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper,
                IterablePath handCodedPath, MCProdSymbol ruleSymbol, boolean isScopeSpanningSymbol);

}
