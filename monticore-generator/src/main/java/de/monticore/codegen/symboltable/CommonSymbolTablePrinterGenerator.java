/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.codegen.symboltable;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTScopeRule;
import de.monticore.grammar.grammar._ast.ASTSymbolRule;
import de.monticore.io.paths.IterablePath;
import de.se_rwth.commons.Names;

public class CommonSymbolTablePrinterGenerator implements SymbolTablePrinterGenerator {
  
  protected static final String TEMPLATE = "symboltable.serialization.SymbolTablePrinter";
  
  /**
   * @see de.monticore.codegen.symboltable.SymbolTablePrinterGenerator#generate(de.monticore.generating.GeneratorEngine,
   * de.monticore.codegen.symboltable.SymbolTableGeneratorHelper,
   * de.monticore.io.paths.IterablePath, java.lang.String, java.lang.String, java.lang.String,
   * java.util.Collection)
   */
  @Override
  public void generate(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper,
      IterablePath handCodedPath, String languageName) {
    
    String className = GeneratorHelper.getSimpleTypeNameToGenerate(
        Names.getSimpleName(languageName + "SymbolTablePrinter"),
        genHelper.getSerializationTargetPackage(), handCodedPath);
    
    final Path filePath = Paths.get(
        Names.getPathFromPackage(genHelper.getSerializationTargetPackage()), className + ".java");
    
    ASTMCGrammar grammar = genHelper.getGrammarSymbol().getAstGrammar().get();
    Optional<ASTScopeRule> scopeRule = grammar.getScopeRulesOpt();
    Map<String, ASTSymbolRule> symbolRules = getSymbolRules( grammar);
    
    genEngine.generateNoA(TEMPLATE, filePath, languageName, className,
        genHelper.getSymbolTablePackage(), genHelper.getVisitorPackage(),
        genHelper.getAllSymbolDefiningRules(), scopeRule, symbolRules);
  }
  
  protected Map<String, ASTSymbolRule> getSymbolRules(ASTMCGrammar grammar) {
    Map<String, ASTSymbolRule> result = new HashMap<String, ASTSymbolRule>();
    for(ASTSymbolRule rule : grammar.getSymbolRuleList()) {
      result.put(rule.getType(), rule);
    }
    return result;
  }
}
