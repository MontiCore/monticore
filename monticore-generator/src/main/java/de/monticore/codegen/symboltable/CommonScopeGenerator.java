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
import java.util.Optional;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTScopeRule;
import de.monticore.io.paths.IterablePath;
import de.se_rwth.commons.Names;

public class CommonScopeGenerator implements ScopeGenerator {
  
  @Override
  public void generate(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper, IterablePath handCodedPath,
      String scopeName) {
    generateScope(genEngine, genHelper, handCodedPath, scopeName);
  }
  
  protected void generateScope(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper, IterablePath handCodedPath,
      String scopeName) {
    String className = getSimpleTypeNameToGenerate(getSimpleName(scopeName),
        genHelper.getTargetPackage(), handCodedPath);
    
    String builderName = getSimpleTypeNameToGenerate(getSimpleName(scopeName +  GeneratorHelper.BUILDER),
        genHelper.getTargetPackage(), handCodedPath);

    final Path filePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()), className + ".java");
    final Path builderFilePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()), builderName + ".java");
    ASTMCGrammar grammar = genHelper.getGrammarSymbol().getAstGrammar().get();
    Optional<ASTScopeRule> scopeRule = Optional.empty();
    if (!grammar.isEmptyScopeRules()) {
      scopeRule = Optional.of(grammar.getScopeRule(0));
    }
    genEngine.generateNoA("symboltable.Scope", filePath, className, scopeRule);
    genEngine.generateNoA("symboltable.ScopeBuilder", builderFilePath, builderName, scopeName + GeneratorHelper.BUILDER);
  }
}
