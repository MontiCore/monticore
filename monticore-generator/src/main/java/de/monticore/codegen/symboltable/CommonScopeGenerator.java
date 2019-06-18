/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.codegen.symboltable;

import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTScopeRule;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.io.paths.IterablePath;
import de.se_rwth.commons.Names;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static de.monticore.codegen.GeneratorHelper.existsHandwrittenClass;
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
    
    String _package = genHelper.getTargetPackage();
  
    String baseNameClass = getSimpleName(scopeName);
    String baseNameInterface = "I" + getSimpleName(scopeName);
    String baseNameArtifactScope = getSimpleName(languageName+GeneratorHelper.ARTIFACT_SCOPE);
    String baseNameGlobalScope = getSimpleName(languageName+GeneratorHelper.GLOBAL_SCOPE);
    String baseNameGlobalScopeInterface = getSimpleName("I"+languageName+GeneratorHelper.GLOBAL_SCOPE);
  
  
    boolean existsHWCScopeImpl = existsHandwrittenClass(baseNameClass, _package , handCodedPath);
    boolean existsHWCArtifactScopeImpl = existsHandwrittenClass(baseNameArtifactScope, _package, handCodedPath);
    boolean existsHWCGlobalScopeImpl = existsHandwrittenClass(baseNameGlobalScope, _package, handCodedPath);
  
    String scopeClassName = getSimpleTypeNameToGenerate(baseNameClass, _package, handCodedPath);
  
    
  
    String interfaceName = getSimpleTypeNameToGenerate(baseNameInterface,
        _package, handCodedPath);

    String builderName = getSimpleTypeNameToGenerate(
        getSimpleName(scopeName + GeneratorHelper.BUILDER), _package, handCodedPath);
    
    String deserName = getSimpleTypeNameToGenerate(getSimpleName(scopeName + GeneratorHelper.DESER),
        genHelper.getSerializationTargetPackage(), handCodedPath);
    
    String artifactScopeClassName = getSimpleTypeNameToGenerate(baseNameArtifactScope,
        _package, handCodedPath);
    
    String globalScopeClassName = getSimpleTypeNameToGenerate(baseNameGlobalScope, _package, handCodedPath);
    
    String globalScopeInterfaceClassName = getSimpleTypeNameToGenerate(baseNameGlobalScopeInterface, _package, handCodedPath);
    
    
    

    // Maps Symbol Name to Symbol Kind Name
    Map<String, String> symbolNames = new HashMap<String, String>();
    Map<String, String> spanningSymbolNames = new HashMap<String, String>();
    for (MCProdSymbol sym : allSymbolDefiningRules) {
      String name = getSimpleName(sym.getName());
      String kind;
      if (sym.getSymbolDefinitionKind().isPresent()) {
        kind = getSimpleName(sym.getSymbolDefinitionKind().get() + GeneratorHelper.SYMBOL);
      } else {
        kind = name + GeneratorHelper.SYMBOL;
      }
      symbolNames.put(name, kind);
      if(sym.isScopeDefinition()){
        spanningSymbolNames.put(name,kind);
      }
    }
  
    Map<String, String> allSymbols = new HashMap<String, String>();
    Map<String, String> allSpanningSymbolNames = new HashMap<String, String>();
    for (MCProdSymbol sym : allSymbolDefiningRulesWithSuperGrammar) {
      String name = getSimpleName(sym.getName());
      String kind = genHelper.getQualifiedProdName(sym) + GeneratorHelper.SYMBOL;
      allSymbols.put(name, kind);
      if(sym.isScopeDefinition()){
        allSpanningSymbolNames.put(name,kind);
      }
    }

    // Maps Symbol Name to Symbol Kind Name
    Map<String, String> symbolNamesWithSuperGrammar = new HashMap<>();
    for (MCProdSymbol sym : allSymbolDefiningRulesWithSuperGrammar) {
      String name;
      if (sym.getSymbolDefinitionKind().isPresent()) {
        name = getSimpleName(sym.getSymbolDefinitionKind().get() + GeneratorHelper.SYMBOL);
      } else {
        name =  getSimpleName(sym.getName()) + GeneratorHelper.SYMBOL;
      }
      String qualifiedName = genHelper.getQualifiedProdName(sym) + GeneratorHelper.SYMBOL;
      symbolNamesWithSuperGrammar.put(name, qualifiedName);
    }

    // symbols that got overwritten by a nonterminal
    // needed so the scope does implement all methods from the interface
    // discuss if this is even allowed to do
    for (MCProdSymbol sym : genHelper.getAllOverwrittenSymbolProductions()) {
      String name;
      if (sym.getSymbolDefinitionKind().isPresent()) {
        name = getSimpleName(sym.getSymbolDefinitionKind().get() + GeneratorHelper.SYMBOL);
      } else {
        name =  getSimpleName(sym.getName()) + GeneratorHelper.SYMBOL;
      }
      String qualifiedName = genHelper.getQualifiedProdName(sym) + GeneratorHelper.SYMBOL;
      symbolNamesWithSuperGrammar.put(name, qualifiedName);
    }

    // list of all superscope interfaces of the current scope
    Set<String> allSuperScopes = new HashSet<>();
    for (CDDefinitionSymbol cdSymbol : genHelper.getAllSuperCds(genHelper.getCd())) {
      if (genHelper.hasSymbolTable(cdSymbol.getFullName())) {
        String qualifiedSymbolName = genHelper.getQualifiedScopeInterfaceType(cdSymbol);
        if (!qualifiedSymbolName.isEmpty()) {
          allSuperScopes.add(qualifiedSymbolName);
        }
      }
    }

    // list of local superscope interfaces that the interface must extend
    Set<String> localSuperScopes = new HashSet<>();
    for (String symbol : genHelper.getSuperGrammarCds()) {
      if (genHelper.hasSymbolTable(symbol)) {
        String qualifiedSymbolName = genHelper.getQualifiedScopeInterfaceType(symbol);
        if (!qualifiedSymbolName.isEmpty()) {
        	localSuperScopes.add(qualifiedSymbolName);
        }
      }
    }
    
    // list of superscopevisitors that the scope must accept
    Set<String> superScopeVisitors = new HashSet<>();
    for (CDDefinitionSymbol cdSymbol : genHelper.getAllSuperCds(genHelper.getCd())) {
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
        .get(Names.getPathFromPackage(genHelper.getSerializationTargetPackage()), deserName + ".java");
    final Path artifactScopeFilePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()),
        artifactScopeClassName + ".java");
    final Path globalScopeFilePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()),
        globalScopeClassName + ".java");
    final Path globalScopeInterfaceFilePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()),
        globalScopeInterfaceClassName + ".java");
    



    ASTMCGrammar grammar = genHelper.getGrammarSymbol().getAstGrammar().get();
    Optional<ASTScopeRule> scopeRule = grammar.getScopeRulesOpt();
    genEngine.generateNoA("symboltable.Scope", scopeFilePath, scopeClassName, baseNameInterface, scopeRule, symbolNamesWithSuperGrammar, allSuperScopes, superScopeVisitors,existsHWCScopeImpl);
    genEngine.generateNoA("symboltable.ScopeInterface", interfaceFilePath, interfaceName, symbolNames, localSuperScopes, languageName);
    genEngine.generateNoA("symboltable.ScopeBuilder", builderFilePath, builderName, scopeName);
  
    if(genHelper.getGrammarSymbol().getStartProd().isPresent()) {
      genEngine.generateNoA("symboltable.serialization.ScopeDeSer", serializationFilePath, languageName , deserName, scopeRule, symbolNames,spanningSymbolNames);
    
      genEngine.generateNoA("symboltable.ArtifactScope", artifactScopeFilePath, artifactScopeClassName, baseNameClass, languageName, symbolNames,existsHWCArtifactScopeImpl);
   
      genEngine.generateNoA("symboltable.GlobalScope", globalScopeFilePath, globalScopeClassName,
          languageName, baseNameGlobalScopeInterface, allSymbolDefiningRulesWithSuperGrammar, existsHWCGlobalScopeImpl);
    
      genEngine.generateNoA("symboltable.GlobalScopeInterface", globalScopeInterfaceFilePath,
          globalScopeInterfaceClassName, baseNameInterface, languageName, allSymbols);
    }

  }
}