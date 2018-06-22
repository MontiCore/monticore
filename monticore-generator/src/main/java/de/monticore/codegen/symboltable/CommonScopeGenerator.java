/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.codegen.symboltable;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.io.paths.IterablePath;
import de.se_rwth.commons.Names;

import java.nio.file.Path;
import java.nio.file.Paths;

import static de.monticore.codegen.GeneratorHelper.getPackageName;

public class CommonScopeGenerator implements ScopeGenerator {
  
  @Override
  public void generate(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper, IterablePath handCodedPath,
      MCProdSymbol ruleSymbol) {
    generateScope(genEngine, genHelper, handCodedPath, ruleSymbol);
  }
  
  protected void generateScope(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper, IterablePath handCodedPath,
      MCProdSymbol ruleSymbol) {
    final String className = genHelper.getScopeClassName(ruleSymbol);
    final String qualifiedClassName = getPackageName(genHelper.getTargetPackage(), "") + className;
    
    if(TransformationHelper.existsHandwrittenClass(handCodedPath, qualifiedClassName)) {
      // Scope classes are very simple and small. Hence, skip their generation
      // if handwritten class exists.
      return;
    }
    
    final Path filePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()), className + ".java");
    final Path builderFilePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()), className + GeneratorHelper.BUILDER + ".java");
    if (ruleSymbol.getAstNode().isPresent()) {
      genEngine.generate("symboltable.Scope", filePath, ruleSymbol.getAstNode().get(), className);
      genEngine.generate("symboltable.ScopeBuilder", builderFilePath, ruleSymbol.getAstNode().get(), className);
    }
  }
}
