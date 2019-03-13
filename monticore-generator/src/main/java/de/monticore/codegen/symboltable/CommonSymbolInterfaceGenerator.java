/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.codegen.symboltable;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTScopeRule;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.io.paths.IterablePath;
import de.se_rwth.commons.Names;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static de.monticore.codegen.GeneratorHelper.getSimpleTypeNameToGenerate;
import static de.se_rwth.commons.Names.getSimpleName;

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