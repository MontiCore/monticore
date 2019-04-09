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
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.io.paths.IterablePath;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.se_rwth.commons.Names;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static de.monticore.codegen.GeneratorHelper.getSimpleTypeNameToGenerate;
import static de.se_rwth.commons.Names.getSimpleName;

public class CommonScopeGenerator implements ScopeGenerator {

  @Override
  public void generate(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper,
                       IterablePath handCodedPath,
                       String scopeName, Collection<MCProdSymbol> allSymbolDefiningRules, Collection<MCProdSymbol> allSymbolDefiningRulesWithSuperGrammar) {
    generateScope(genEngine, genHelper, handCodedPath, scopeName, allSymbolDefiningRules, allSymbolDefiningRulesWithSuperGrammar);
  }

  protected void generateScope(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper,
                               IterablePath handCodedPath,
                               String scopeName, Collection<MCProdSymbol> allSymbolDefiningRules, Collection<MCProdSymbol> allSymbolDefiningRulesWithSuperGrammar) {
    
    final String languageName = genHelper.getGrammarSymbol().getName();
    
    String scopeClassName = getSimpleTypeNameToGenerate(getSimpleName(scopeName),
        genHelper.getTargetPackage(), handCodedPath);

    String builderName = getSimpleTypeNameToGenerate(
        getSimpleName(scopeName + GeneratorHelper.BUILDER),
        genHelper.getTargetPackage(), handCodedPath);
    
    String deserName = getSimpleTypeNameToGenerate(
        getSimpleName(scopeName + GeneratorHelper.DESER),
        genHelper.getTargetPackage()+".serialization", handCodedPath);
    
    String artifactScopeClassName = getSimpleTypeNameToGenerate(getSimpleName(languageName+GeneratorHelper.ARTIFACT_SCOPE),
        genHelper.getTargetPackage(), handCodedPath);
    
    String globalScopeClassName = getSimpleTypeNameToGenerate(getSimpleName(languageName+GeneratorHelper.GLOBAL_SCOPE),
        genHelper.getTargetPackage(), handCodedPath);
    
    String globalScopeInterfaceClassName = getSimpleTypeNameToGenerate(getSimpleName("I"+languageName+GeneratorHelper.GLOBAL_SCOPE),
        genHelper.getTargetPackage(), handCodedPath);
    
    
    String interfaceName = "I" + scopeClassName;

    // Maps Symbol Name to Symbol Kind Name
    Map<String, String> symbolNames = new HashMap<String, String>();
    for (MCProdSymbol sym : allSymbolDefiningRules) {
      String name = getSimpleName(sym.getName());
      String kind;
      if (sym.getSymbolDefinitionKind().isPresent()) {
        kind = getSimpleName(sym.getSymbolDefinitionKind().get() + GeneratorHelper.SYMBOL);
      } else {
        kind = name + GeneratorHelper.SYMBOL;
      }
      symbolNames.put(name, kind);
    }

    // Maps Symbol Name to Symbol Kind Name
    Map<String, String> symbolNamesWithSuperGrammar = new HashMap<>();
    for (MCProdSymbol sym : allSymbolDefiningRulesWithSuperGrammar) {
      String name = getSimpleName(sym.getName());
      String kind = genHelper.getQualifiedProdName(sym) + GeneratorHelper.SYMBOL;
      symbolNamesWithSuperGrammar.put(name, kind);
    }

    // symbols that got overwritten by a nonterminal
    // needed so the scope does implement all methods from the interface
    // discuss if this is even allowed to do
    for (MCProdSymbol sym : genHelper.getAllOverwrittenSymbolProductions()) {
      String name = getSimpleName(sym.getName());
      String kind = genHelper.getQualifiedProdName(sym) + GeneratorHelper.SYMBOL;
      symbolNamesWithSuperGrammar.put(name, kind);
    }

    //list of superscopes that the interface must extend
    Set<String> superScopes = new HashSet<>();
    for (String symbol : genHelper.getSuperGrammarCds()) {
      if (!genHelper.isComponentGrammar(symbol)) {
        String qualifiedSymbolName = genHelper.getQualifiedScopeInterfaceType(symbol);
        if (!qualifiedSymbolName.isEmpty()) {
          superScopes.add(qualifiedSymbolName);
        }
      }
    }
    
    // list of superscopevisitors that the scope must accept
    Set<String> superScopeVisitors = new HashSet<>();
    for (CDSymbol cdSymbol : genHelper.getAllSuperCds(genHelper.getCd())) {
      String qualifiedScopeVisitorName = genHelper.getQualifiedScopeVisitorType(cdSymbol);
      if (!qualifiedScopeVisitorName.isEmpty()) {
        superScopeVisitors.add(qualifiedScopeVisitorName);
      }
    }
    
    final Path scopeFilePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()),
        scopeClassName + ".java");
    final Path builderFilePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()),
        builderName + ".java");
    final Path interfaceFilePath = Paths
        .get(Names.getPathFromPackage(genHelper.getTargetPackage()), interfaceName + ".java");
    final Path serializationFilePath = Paths
        .get(Names.getPathFromPackage(genHelper.getTargetPackage()),"serialization", deserName + ".java");
    final Path artifactScopeFilePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()),
        artifactScopeClassName + ".java");
    final Path globalScopeFilePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()),
        globalScopeClassName + ".java");
    final Path globalScopeInterfaceFilePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()),
        globalScopeInterfaceClassName + ".java");
    



    ASTMCGrammar grammar = genHelper.getGrammarSymbol().getAstGrammar().get();
    Optional<ASTScopeRule> scopeRule = grammar.getScopeRulesOpt();
    genEngine.generateNoA("symboltable.Scope", scopeFilePath, scopeClassName, scopeRule, symbolNames, superScopeVisitors);
    genEngine.generateNoA("symboltable.ScopeInterface", interfaceFilePath, interfaceName, symbolNames, superScopes, languageName);
    genEngine.generateNoA("symboltable.ScopeBuilder", builderFilePath, builderName, scopeName);
    genEngine.generateNoA("symboltable.serialization.ScopeDeSer", serializationFilePath, languageName , deserName, scopeRule, symbolNames);

    genEngine.generateNoA("symboltable.ArtifactScope", artifactScopeFilePath, artifactScopeClassName, scopeClassName, languageName, symbolNames);
    genEngine.generateNoA("symboltable.GlobalScope", globalScopeFilePath, globalScopeClassName, languageName);
    genEngine.generateNoA("symboltable.GlobalScopeInterface", globalScopeInterfaceFilePath, globalScopeInterfaceClassName, scopeClassName, languageName, symbolNames);
    

  }
}