/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.symboltable;

import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTScopeRule;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.io.paths.IterablePath;
import de.se_rwth.commons.Names;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static de.monticore.codegen.GeneratorHelper.*;
import static de.se_rwth.commons.Names.getSimpleName;

public class CommonScopeGenerator implements ScopeGenerator {

  @Override
  public void generate(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper,
                       IterablePath handCodedPath,
                       String scopeName, Collection<ProdSymbol> allSymbolDefiningRules, Collection<ProdSymbol> allSymbolDefiningRulesWithSuperGrammar) {
    generateScope(genEngine, genHelper, handCodedPath, scopeName, allSymbolDefiningRules, allSymbolDefiningRulesWithSuperGrammar);
  }

  protected void generateScope(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper,
                               IterablePath handCodedPath,
                               String scopeName, Collection<ProdSymbol> allSymbolDefiningRules, Collection<ProdSymbol> allSymbolDefiningRulesWithSuperGrammar) {

    final String languageName = genHelper.getGrammarSymbol().getName();

    String _package = genHelper.getTargetPackage();

    String baseNameClass = getSimpleName(scopeName);
    String baseNameInterface = "I" + getSimpleName(scopeName);
    String baseNameArtifactScope = getSimpleName(languageName+GeneratorHelper.ARTIFACT_SCOPE);
    String baseNameArtifactScopeBuilder = getSimpleName(languageName+GeneratorHelper.ARTIFACT_SCOPE+"Builder");
    String baseNameGlobalScope = getSimpleName(languageName+GeneratorHelper.GLOBAL_SCOPE);
    String baseNameGlobalScopeBuilder = getSimpleName(languageName+GeneratorHelper.GLOBAL_SCOPE+"Builder");
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

    String artifactScopeBuilderClassName = getSimpleTypeNameToGenerate(baseNameArtifactScopeBuilder,_package,handCodedPath);

    String globalScopeClassName = getSimpleTypeNameToGenerate(baseNameGlobalScope, _package, handCodedPath);

    String globalScopeBuilderClassName = getSimpleTypeNameToGenerate(baseNameGlobalScopeBuilder,_package,handCodedPath);

    String globalScopeInterfaceClassName = getSimpleTypeNameToGenerate(baseNameGlobalScopeInterface, _package, handCodedPath);




    // Maps Symbol Name to Symbol Kind Name
    Map<String, String> symbolNames = new HashMap<String, String>();
    for (ProdSymbol sym : allSymbolDefiningRules) {
      String name = getSimpleName(sym.getName());
      String kind;
      if (sym.isSymbolDefinition()) {
        kind = getSimpleName(sym.getName() + GeneratorHelper.SYMBOL);
      } else {
        kind = name + GeneratorHelper.SYMBOL;
      }
      symbolNames.put(name, kind);
    }

    Map<String, String> symbolNamesWithSuper = new HashMap<String, String>();
    for (ProdSymbol sym : allSymbolDefiningRulesWithSuperGrammar) {
      String packageName = sym.getFullName().substring(0, sym.getFullName().lastIndexOf(".")+1).toLowerCase() + SYMBOLTABLE_PACKAGE_SUFFIX+".";
      String name = getSimpleName(sym.getName());
      String kind;
      if (sym.isSymbolDefinition()) {
        kind = getSimpleName(sym.getName() + GeneratorHelper.SYMBOL);
      } else {
        kind = name + GeneratorHelper.SYMBOL;
      }
      symbolNamesWithSuper.put(name, packageName+kind);
    }

    Map<String, String> allSymbols = new HashMap<String, String>();
    Map<String, String> allSpanningSymbolNames = new HashMap<String, String>();
    for (ProdSymbol sym : allSymbolDefiningRulesWithSuperGrammar) {
      String name = getSimpleName(sym.getName());
      String kind = genHelper.getQualifiedProdName(sym) + GeneratorHelper.SYMBOL;
      allSymbols.put(name, kind);
      if(sym.isScopeSpanning()){
        allSpanningSymbolNames.put(name,kind);
      }
    }

    // Maps Symbol Name to Symbol Kind Name
    Map<String, String> symbolNamesWithSuperGrammar = new HashMap<>();
    for (ProdSymbol sym : allSymbolDefiningRulesWithSuperGrammar) {
      String name =  getSimpleName(sym.getName()) + GeneratorHelper.SYMBOL;
      String qualifiedName = genHelper.getQualifiedProdName(sym) + GeneratorHelper.SYMBOL;
      symbolNamesWithSuperGrammar.put(name, qualifiedName);
    }

    // symbols that got overwritten by a nonterminal
    // needed so the scope does implement all methods from the interface
    // discuss if this is even allowed to do
    for (ProdSymbol sym : genHelper.getAllOverwrittenSymbolProductions()) {
      String name =  getSimpleName(sym.getName()) + GeneratorHelper.SYMBOL;
      String qualifiedName = genHelper.getQualifiedProdName(sym) + GeneratorHelper.SYMBOL;
      symbolNamesWithSuperGrammar.put(name, qualifiedName);
    }

    // list of all superscope interfaces of the current scope
    Set<String> allSuperScopes = new HashSet<>();
    for (CDDefinitionSymbol cdSymbol : genHelper.getAllSuperCds(genHelper.getCd())) {
      String qualifiedSymbolName = genHelper.getQualifiedScopeInterfaceType(cdSymbol);
      if (!qualifiedSymbolName.isEmpty()) {
        allSuperScopes.add(qualifiedSymbolName);
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
  
    List<String> superGrammarPackages = Lists.newArrayList();
    for (CDDefinitionSymbol cdSymbol : genHelper.getAllSuperCds(genHelper.getCd())) {
      if (genHelper.hasSymbolTable(cdSymbol.getFullName())) {
        String qualifiedSymbolName = Names.getQualifier(genHelper.getQualifiedScopeInterfaceType(cdSymbol));
        if (!qualifiedSymbolName.isEmpty()) {
          superGrammarPackages.add(qualifiedSymbolName);
        }
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
    final Path artifactScopeBuilderFilePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()),
        artifactScopeBuilderClassName+".java");
    final Path globalScopeFilePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()),
        globalScopeClassName + ".java");
    final Path globalScopeBuilderFilePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()),
        globalScopeBuilderClassName+".java");
    final Path globalScopeInterfaceFilePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()),
        globalScopeInterfaceClassName + ".java");




    ASTMCGrammar grammar = genHelper.getGrammarSymbol().getAstGrammar().get();
    List<ASTScopeRule> scopeRules = Lists.newArrayList();
    grammar.getScopeRuleOpt().ifPresent(s -> scopeRules.add(s));
    for (MCGrammarSymbol grammarSymbol: genHelper.getGrammarSymbol().getAllSuperGrammars()) {
      grammarSymbol.getAstGrammar().get().getScopeRuleOpt().ifPresent(s -> scopeRules.add(s));
    }
    Optional<ASTScopeRule> scopeRule = grammar.getScopeRuleOpt();
    if(genHelper.getGrammarSymbol().getStartProd().isPresent()) {
    }

  }
}
