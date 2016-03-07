/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.codegen.symboltable;

import java.io.File;
import java.util.Collection;
import java.util.stream.Collectors;

import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.io.paths.IterablePath;
import de.monticore.languages.grammar.MCGrammarSymbol;
import de.monticore.languages.grammar.MCRuleComponentSymbol;
import de.monticore.languages.grammar.MCRuleSymbol;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ScopeSpanningSymbol;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

/**
 * @author Pedram Mir Seyed Nazari
 */
// TODO PN now that it runs, make it beautiful (+ all associated classes)
public class SymbolTableGenerator {

  public static final String PACKAGE = "_symboltable";
  public static final String LOG = SymbolTableGenerator.class.getSimpleName();

  private final SymbolGenerator symbolGenerator;
  private final ScopeSpanningSymbol scopeSpanningSymbolGenerator;

  public SymbolTableGenerator(SymbolGenerator symbolGenerator, ScopeSpanningSymbol scopeSpanningSymbolGenerator) {
    this.symbolGenerator = symbolGenerator;
    this.scopeSpanningSymbolGenerator = scopeSpanningSymbolGenerator;
  }

  public static void generate(ASTMCGrammar astGrammar, GlobalScope globalScope, ASTCDCompilationUnit astCd,
      File outputPath, final IterablePath handCodedPath) {

    SymbolTableGeneratorHelper genHelper = new SymbolTableGeneratorHelper(astGrammar, globalScope, astCd);
    MCGrammarSymbol grammarSymbol = genHelper.getGrammarSymbol();

    // TODO PN also generate for components grammars everything that is possible and useful.
    if (grammarSymbol.isComponent()) {
      return;
    }

    // Skip generation if no rules are defined in the grammar, since no top asts
    // will be generated.
    if (!grammarSymbol.getStartRule().isPresent()) {
      return;
    }
    Log.debug("Start symbol table generation for the grammar " + astGrammar.getName(), LOG);

    // TODO PN consider only class rules?
    final Collection<MCRuleSymbol> allSymbolDefiningRules = genHelper.getAllSymbolDefiningRules();
    final Collection<String> ruleNames = allSymbolDefiningRules.stream()
        .map(MCRuleSymbol::getName).collect(Collectors.toSet());

    /*
     * If no rules with name are defined, no symbols can be generated.
     * Hence, skip generation of: ModelNameCalculator, SymbolTableCreator,
     * Symbol, SymbolKind, SymbolReference, ScopeSpanningSymbol, (Spanned) Scope
     * and ResolvingFilter
     *
     */
    final boolean skipSymbolTableGeneration = allSymbolDefiningRules.isEmpty();

    final GeneratorSetup setup = new GeneratorSetup(outputPath);
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    glex.setGlobalValue("stHelper", genHelper);
    glex.setGlobalValue("nameHelper", new Names());
    glex.setGlobalValue("skipSTGen", skipSymbolTableGeneration);
    setup.setGlex(glex);

    final GeneratorEngine generator = new GeneratorEngine(setup);

    new CommonModelingLanguageGenerator(generator, genHelper, handCodedPath).generate(grammarSymbol, ruleNames);
    new CommonModelLoaderGenerator(generator, genHelper, handCodedPath).generate(grammarSymbol);

    if (!skipSymbolTableGeneration) {
      new CommonModelNameCalculatorGenerator(generator, genHelper, handCodedPath).generate(grammarSymbol, ruleNames);
      new CommonSymbolTableCreatorGenerator(generator, genHelper, handCodedPath).generate(grammarSymbol);

      for (MCRuleSymbol ruleSymbol : allSymbolDefiningRules) {
        generateSymbolOrScopeSpanningSymbol(generator, genHelper, ruleSymbol, handCodedPath);
        new CommonSymbolKindGenerator(generator, genHelper, handCodedPath).generate(ruleSymbol);
        new CommonSymbolReferenceGenerator(generator, genHelper, handCodedPath).generate(ruleSymbol, isScopeSpanningSymbol(genHelper, ruleSymbol));
        new CommonResolvingFilterGenerator(generator, genHelper, handCodedPath).generate(ruleSymbol);
      }
    }

    Log.debug("End symbol table generation for the grammar " + astGrammar.getName(), LOG);
  }

  private static void generateSymbolOrScopeSpanningSymbol(GeneratorEngine generator, SymbolTableGeneratorHelper genHelper,
      MCRuleSymbol ruleSymbol, IterablePath handCodedPath) {
    if (ruleSymbol.getAstNode().isPresent()) {
      if (!isScopeSpanningSymbol(genHelper, ruleSymbol)) {
         new CommonSymbolGenerator(generator, genHelper, handCodedPath).generate(ruleSymbol);
      }
      else {
        new CommonScopeSpanningSymbolGenerator(generator, genHelper, handCodedPath).generate(ruleSymbol);
      }
    }
  }

  private static boolean isScopeSpanningSymbol(SymbolTableGeneratorHelper genHelper, final MCRuleSymbol rule) {
    for (MCRuleComponentSymbol ruleComponent : rule.getRuleComponents()) {
      final MCRuleSymbol referencedRule = genHelper.getGrammarSymbol().getRule(ruleComponent.getReferencedRuleName());
      if ((referencedRule != null) && referencedRule.isSymbolDefinition()) {
        return true;
      }
    }

    return false;
  }

}
