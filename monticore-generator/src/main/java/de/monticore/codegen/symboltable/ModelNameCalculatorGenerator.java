/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.symboltable;

import java.util.Collection;

import de.monticore.generating.GeneratorEngine;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.io.paths.IterablePath;

/**
 * @author Pedram Mir Seyed Nazari
 */
public interface ModelNameCalculatorGenerator {

  void generate(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper,
      IterablePath handCodedPath, MCGrammarSymbol grammarSymbol, Collection<String> grammarRuleNames);

}
