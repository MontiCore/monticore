/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.symboltable;

import com.google.common.collect.Lists;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTSymbolRule;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.io.paths.IterablePath;
import de.se_rwth.commons.Names;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static de.monticore.codegen.GeneratorHelper.SCOPE;
import static de.se_rwth.commons.logging.Log.debug;

public class SymbolTableGenerator {

  public static final String PACKAGE = "_symboltable";

  public static final String LOG = SymbolTableGenerator.class.getSimpleName();

  private final ModelingLanguageGenerator modelingLanguageGenerator;

  private final ModelLoaderGenerator modelLoaderGenerator;

  private final ModelNameCalculatorGenerator modelNameCalculatorGenerator;

  private final ResolvingFilterGenerator resolvingFilterGenerator;

  private final SymbolGenerator symbolGenerator;

  private final SymbolKindGenerator symbolKindGenerator;

  private final ScopeSpanningSymbolGenerator scopeSpanningSymbolGenerator;

  private final ScopeGenerator scopeGenerator;

  private final SymbolReferenceGenerator symbolReferenceGenerator;

  private final SymbolTableCreatorGenerator symbolTableCreatorGenerator;

  private final SymbolInterfaceGenerator symbolInterfaceGenerator;

  protected SymbolTableGenerator(
      ModelingLanguageGenerator modelingLanguageGenerator,
      ModelLoaderGenerator modelLoaderGenerator,
      ModelNameCalculatorGenerator modelNameCalculatorGenerator,
      ResolvingFilterGenerator resolvingFilterGenerator,
      SymbolGenerator symbolGenerator,
      SymbolKindGenerator symbolKindGenerator,
      ScopeSpanningSymbolGenerator scopeSpanningSymbolGenerator,
      ScopeGenerator scopeGenerator,
      SymbolReferenceGenerator symbolReferenceGenerator,
      SymbolTableCreatorGenerator symbolTableCreatorGenerator,
      SymbolInterfaceGenerator symbolInterfaceGenerator) {
    this.modelingLanguageGenerator = modelingLanguageGenerator;
    this.modelLoaderGenerator = modelLoaderGenerator;
    this.modelNameCalculatorGenerator = modelNameCalculatorGenerator;
    this.resolvingFilterGenerator = resolvingFilterGenerator;
    this.symbolGenerator = symbolGenerator;
    this.symbolKindGenerator = symbolKindGenerator;
    this.scopeGenerator = scopeGenerator;
    this.scopeSpanningSymbolGenerator = scopeSpanningSymbolGenerator;
    this.symbolReferenceGenerator = symbolReferenceGenerator;
    this.symbolTableCreatorGenerator = symbolTableCreatorGenerator;
    this.symbolInterfaceGenerator = symbolInterfaceGenerator;
  }

  public void generate(GlobalExtensionManagement glex, ASTMCGrammar astGrammar, SymbolTableGeneratorHelper genHelper,
                       File outputPath, final IterablePath handCodedPath) {
    
    // Always set symbol table helper even if symbol table is not generated to
    // provide corresponding information and prevent wrong symbol assignments.
    glex.setGlobalValue("stHelper", genHelper);
    
    MCGrammarSymbol grammarSymbol = genHelper.getGrammarSymbol();

    // Skip generation if no rules are defined in the grammar, since no top asts
    // will be generated.
    if (!grammarSymbol.getStartProd().isPresent()) {
      return;
    }
    debug("Start symbol table generation for the grammar " + astGrammar.getName(), LOG);

    // TODO PN consider only class rules?
    final Collection<MCProdSymbol> allSymbolDefiningRules = genHelper.getAllSymbolDefiningRules();
    final Collection<MCProdSymbol> allSymbolDefiningRulesWithSuperGrammar = genHelper.getAllSymbolDefiningRulesInSuperGrammar();
    final Collection<MCProdSymbol> allScopeSpanningRules = genHelper.getAllScopeSpanningRules();
    Collection<String> ruleNames = newArrayList();
    Collection<String> ruleNamesWithSuperGrammar = newArrayList();
    for (MCProdSymbol prod : allSymbolDefiningRules) {
      ruleNames.add(prod.getSymbolDefinitionKind().isPresent() ? prod.getSymbolDefinitionKind().get() : prod.getName());
    }

    for (MCProdSymbol prod : genHelper.getAllSymbolDefiningRulesInSuperGrammar()) {
      ruleNamesWithSuperGrammar.add(genHelper.getQualifiedProdName(prod));
    }

    /* If no rules with name are defined, no symbols can be generated. Hence,
     * skip generation of: ModelNameCalculator, SymbolTableCreator, Symbol,
     * SymbolKind, SymbolReference, ScopeSpanningSymbol, (Spanned) Scope and
     * ResolvingFilter */
    final boolean skipSymbolTableGeneration = allSymbolDefiningRules.isEmpty() && allScopeSpanningRules.isEmpty();

    final GeneratorSetup setup = new GeneratorSetup();
    setup.setOutputDirectory(outputPath);
    glex.setGlobalValue("nameHelper", new Names());
    glex.setGlobalValue("skipSTGen", skipSymbolTableGeneration);
    setup.setGlex(glex);

    final GeneratorEngine genEngine = new GeneratorEngine(setup);

    modelingLanguageGenerator.generate(genEngine, genHelper, handCodedPath, grammarSymbol,
        ruleNamesWithSuperGrammar);
    modelLoaderGenerator.generate(genEngine, genHelper, handCodedPath, grammarSymbol);

    if (!skipSymbolTableGeneration) {
      modelNameCalculatorGenerator.generate(genEngine, genHelper, handCodedPath, grammarSymbol,
          ruleNames);
      symbolTableCreatorGenerator.generate(genEngine, genHelper, handCodedPath, grammarSymbol);

      Collection<ASTSymbolRule> symbolRuleList = genHelper.getGrammarSymbol().getAstGrammar().get().getSymbolRuleList();
      Collection<ASTSymbolRule> refSymbolRuleList = Lists.newArrayList();
      for (MCProdSymbol prodSymbol : allSymbolDefiningRules) {
        Optional<ASTSymbolRule> symbolRule = Optional.empty();
        for (ASTSymbolRule sr : symbolRuleList) {
          if (sr.getType().equals(prodSymbol.getSymbolDefinitionKind().get())) {
            symbolRule = Optional.of(sr);
            ((ArrayList<ASTSymbolRule>) refSymbolRuleList).add(sr);
            break;
          }
        }
        generateSymbolOrScopeSpanningSymbol(genEngine, genHelper, Optional.of(prodSymbol), symbolRule, handCodedPath);
        String className = (prodSymbol.getSymbolDefinitionKind().isPresent() ? prodSymbol.getSymbolDefinitionKind().get() : prodSymbol.getName());
        symbolKindGenerator.generate(genEngine, genHelper, handCodedPath, className);
        symbolReferenceGenerator.generate(genEngine, genHelper, handCodedPath, prodSymbol,
            genHelper.isScopeSpanningSymbol(prodSymbol));
        resolvingFilterGenerator.generate(genEngine, genHelper, handCodedPath, prodSymbol);
      }
      // Generate symbol for symbolrules without corresponding production
      symbolRuleList.removeAll(refSymbolRuleList);
      for (ASTSymbolRule sr: symbolRuleList) {
        generateSymbolOrScopeSpanningSymbol(genEngine, genHelper,
                Optional.<MCProdSymbol>empty(), Optional.of(sr), handCodedPath);
        symbolKindGenerator.generate(genEngine, genHelper, handCodedPath, sr.getType());
      }
    }
    //a symbol interface and scope is generated for all grammars
    symbolInterfaceGenerator.generate(genEngine, genHelper, handCodedPath, grammarSymbol);
    scopeGenerator.generate(genEngine, genHelper, handCodedPath, grammarSymbol.getName() + SCOPE, allSymbolDefiningRules, allSymbolDefiningRulesWithSuperGrammar);


    debug("End symbol table generation for the grammar " + astGrammar.getName(), LOG);
  }

  private void generateSymbolOrScopeSpanningSymbol(GeneratorEngine genEngine,
                                                   SymbolTableGeneratorHelper genHelper,
                                                   Optional<MCProdSymbol> prodSymbol, Optional<ASTSymbolRule> symbolRule,
                                                   IterablePath handCodedPath) {
    if (prodSymbol.isPresent() && genHelper.isScopeSpanningSymbol(prodSymbol.get())) {
      scopeSpanningSymbolGenerator.generate(genEngine, genHelper, handCodedPath, prodSymbol.get());
    } else {
      symbolGenerator.generate(genEngine, genHelper, handCodedPath, prodSymbol, symbolRule);
    }
  }

}
