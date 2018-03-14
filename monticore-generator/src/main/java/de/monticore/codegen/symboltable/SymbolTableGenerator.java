/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.symboltable;

import java.io.File;
import java.util.Collection;
import java.util.stream.Collectors;

import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.io.paths.IterablePath;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

/**
 * @author Pedram Mir Seyed Nazari
 */
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
  
  private final SymbolReferenceGenerator symbolReferenceGenerator;
  
  private final SymbolTableCreatorGenerator symbolTableCreatorGenerator;

  private final SymbolMillGenerator symbolMillGenerator;
  
  protected SymbolTableGenerator(
      ModelingLanguageGenerator modelingLanguageGenerator,
      ModelLoaderGenerator modelLoaderGenerator,
      ModelNameCalculatorGenerator modelNameCalculatorGenerator,
      ResolvingFilterGenerator resolvingFilterGenerator,
      SymbolGenerator symbolGenerator,
      SymbolKindGenerator symbolKindGenerator,
      ScopeSpanningSymbolGenerator scopeSpanningSymbolGenerator,
      SymbolReferenceGenerator symbolReferenceGenerator,
      SymbolTableCreatorGenerator symbolTableCreatorGenerator,
      SymbolMillGenerator symbolMillGenerator) {
    this.modelingLanguageGenerator = modelingLanguageGenerator;
    this.modelLoaderGenerator = modelLoaderGenerator;
    this.modelNameCalculatorGenerator = modelNameCalculatorGenerator;
    this.resolvingFilterGenerator = resolvingFilterGenerator;
    this.symbolGenerator = symbolGenerator;
    this.symbolKindGenerator = symbolKindGenerator;
    this.scopeSpanningSymbolGenerator = scopeSpanningSymbolGenerator;
    this.symbolReferenceGenerator = symbolReferenceGenerator;
    this.symbolTableCreatorGenerator = symbolTableCreatorGenerator;
    this.symbolMillGenerator = symbolMillGenerator;
  }
  
  public void generate(ASTMCGrammar astGrammar, SymbolTableGeneratorHelper genHelper,
      File outputPath, final IterablePath handCodedPath) {
    
    MCGrammarSymbol grammarSymbol = genHelper.getGrammarSymbol();
    
    // Skip generation if no rules are defined in the grammar, since no top asts
    // will be generated.
    if (!grammarSymbol.getStartProd().isPresent()) {
      return;
    }
    Log.debug("Start symbol table generation for the grammar " + astGrammar.getName(), LOG);
    
    // TODO PN consider only class rules?
    final Collection<MCProdSymbol> allSymbolDefiningRules = genHelper.getAllSymbolDefiningRules();
    final Collection<String> ruleNames = allSymbolDefiningRules.stream()
        .map(MCProdSymbol::getName).collect(Collectors.toSet());
    
    /* If no rules with name are defined, no symbols can be generated. Hence,
     * skip generation of: ModelNameCalculator, SymbolTableCreator, Symbol,
     * SymbolKind, SymbolReference, ScopeSpanningSymbol, (Spanned) Scope and
     * ResolvingFilter */
    final boolean skipSymbolTableGeneration = allSymbolDefiningRules.isEmpty();
    
    final GeneratorSetup setup = new GeneratorSetup();
    setup.setOutputDirectory(outputPath);
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    glex.setGlobalValue("stHelper", genHelper);
    glex.setGlobalValue("nameHelper", new Names());
    glex.setGlobalValue("skipSTGen", skipSymbolTableGeneration);
    setup.setGlex(glex);
    
    final GeneratorEngine genEngine = new GeneratorEngine(setup);
    
    modelingLanguageGenerator.generate(genEngine, genHelper, handCodedPath, grammarSymbol,
        ruleNames);
    modelLoaderGenerator.generate(genEngine, genHelper, handCodedPath, grammarSymbol);
    
    if (!skipSymbolTableGeneration) {
      modelNameCalculatorGenerator.generate(genEngine, genHelper, handCodedPath, grammarSymbol,
          ruleNames);
      symbolTableCreatorGenerator.generate(genEngine, genHelper, handCodedPath, grammarSymbol);
      symbolMillGenerator.generate(genEngine, genHelper, handCodedPath, grammarSymbol, allSymbolDefiningRules);
      
      for (MCProdSymbol ruleSymbol : allSymbolDefiningRules) {
        generateSymbolOrScopeSpanningSymbol(genEngine, genHelper, ruleSymbol, handCodedPath);
        symbolKindGenerator.generate(genEngine, genHelper, handCodedPath, ruleSymbol);
        symbolReferenceGenerator.generate(genEngine, genHelper, handCodedPath, ruleSymbol,
            genHelper.isScopeSpanningSymbol(ruleSymbol));
        resolvingFilterGenerator.generate(genEngine, genHelper, handCodedPath, ruleSymbol);
      }
    }
    
    Log.debug("End symbol table generation for the grammar " + astGrammar.getName(), LOG);
  }
  
  private void generateSymbolOrScopeSpanningSymbol(GeneratorEngine genEngine,
      SymbolTableGeneratorHelper genHelper,
      MCProdSymbol ruleSymbol, IterablePath handCodedPath) {
    if (ruleSymbol.getAstNode().isPresent()) {
      if (!genHelper.isScopeSpanningSymbol(ruleSymbol)) {
        symbolGenerator.generate(genEngine, genHelper, handCodedPath, ruleSymbol);
      }
      else {
        scopeSpanningSymbolGenerator.generate(genEngine, genHelper, handCodedPath, ruleSymbol);
      }
    }
  }
  
}
