/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.symboltable;

import de.monticore.generating.GeneratorEngine;
import de.monticore.grammar.grammar._ast.ASTSymbolRule;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.io.paths.IterablePath;

import java.util.Optional;

public interface SymbolGenerator {

  void generate(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper,
                IterablePath handCodedPath, Optional<MCProdSymbol> prodSymbol, Optional<ASTSymbolRule> symbolRule);

}
