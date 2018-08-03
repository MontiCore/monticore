/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.codegen.symboltable;

import static de.monticore.codegen.GeneratorHelper.getSimpleTypeNameToGenerate;
import static de.se_rwth.commons.Names.getSimpleName;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTScopeRule;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.io.paths.IterablePath;
import de.se_rwth.commons.Names;

public class CommonScopeGenerator implements ScopeGenerator {
  
  @Override
  public void generate(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper,
      IterablePath handCodedPath,
      String scopeName, Collection<MCProdSymbol> allSymbolDefiningRules) {
    generateScope(genEngine, genHelper, handCodedPath, scopeName, allSymbolDefiningRules);
  }
  
  protected void generateScope(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper,
      IterablePath handCodedPath,
      String scopeName, Collection<MCProdSymbol> allSymbolDefiningRules) {
    String className = getSimpleTypeNameToGenerate(getSimpleName(scopeName),
        genHelper.getTargetPackage(), handCodedPath);
    
    String builderName = getSimpleTypeNameToGenerate(
        getSimpleName(scopeName + GeneratorHelper.BUILDER),
        genHelper.getTargetPackage(), handCodedPath);
    
    String serializerName = getSimpleTypeNameToGenerate(
        getSimpleName(className + GeneratorHelper.SERIALIZER),
        genHelper.getTargetPackage(), handCodedPath);
    
    // Maps Symbol Name to Symbol Kind Name
    Map<String, String> symbolNames = new HashMap<String, String>();
    for (MCProdSymbol sym : allSymbolDefiningRules) {
      String name = getSimpleName(sym.getName());
      String kind;
      if (sym.getSymbolDefinitionKind().isPresent()) {
        kind = getSimpleName(sym.getSymbolDefinitionKind().get() + GeneratorHelper.SYMBOL);
      }
      else {
        kind = name + GeneratorHelper.SYMBOL;
      }
      symbolNames.put(name, kind);
    }
    
    final Path filePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()),
        className + ".java");
    final Path builderFilePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()),
        builderName + ".java");
    final Path serializerFilePath = Paths
        .get(Names.getPathFromPackage(genHelper.getTargetPackage()), serializerName + ".java");
    
    ASTMCGrammar grammar = genHelper.getGrammarSymbol().getAstGrammar().get();
    Optional<ASTScopeRule> scopeRule = grammar.getScopeRulesOpt();
    genEngine.generateNoA("symboltable.Scope", filePath, className, scopeRule, symbolNames);
    genEngine.generateNoA("symboltable.ScopeBuilder", builderFilePath, builderName,
        scopeName + GeneratorHelper.BUILDER);
    genEngine.generateNoA("symboltable.serialization.ScopeSerialization", serializerFilePath,
        className);
  }
}
