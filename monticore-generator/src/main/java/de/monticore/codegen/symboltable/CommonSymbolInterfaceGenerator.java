/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.symboltable;

import java.nio.file.Path;
import java.nio.file.Paths;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.io.paths.IterablePath;
import de.se_rwth.commons.Names;

public class CommonSymbolInterfaceGenerator implements SymbolInterfaceGenerator {


  @Override
  public void generate(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper, IterablePath handCodedPath, MCGrammarSymbol grammarSymbol) {
    generateSymbolInterface(genEngine, genHelper, handCodedPath, grammarSymbol);
  }
  
  protected void generateSymbolInterface(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper, IterablePath handCodedPath, MCGrammarSymbol grammarSymbol) {
    
    String interfaceName = "ICommon" + grammarSymbol.getName() + GeneratorHelper.SYMBOL;
    final Path filePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()),
        interfaceName + ".java");
    genEngine.generateNoA("symboltable.CommonSymbolInterface", filePath, interfaceName);
  }

}
