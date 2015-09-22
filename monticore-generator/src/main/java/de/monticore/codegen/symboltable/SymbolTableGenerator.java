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

import static de.monticore.codegen.GeneratorHelper.getPackageName;
import static de.monticore.codegen.GeneratorHelper.getSimpleTypeNameToGenerate;
import static de.monticore.languages.grammar.MCRuleSymbol.KindSymbolRule.INTERFACEORABSTRACTRULE;
import static de.se_rwth.commons.Names.getSimpleName;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.io.paths.IterablePath;
import de.monticore.languages.grammar.MCGrammarSymbol;
import de.monticore.languages.grammar.MCInterfaceOrAbstractRuleSymbol;
import de.monticore.languages.grammar.MCRuleComponentSymbol;
import de.monticore.languages.grammar.MCRuleSymbol;
import de.monticore.symboltable.GlobalScope;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

/**
 * @author Pedram Mir Seyed Nazari
 */
// TODO PN now that it runs, make it beautiful (+ all associated classes)
public class SymbolTableGenerator {

  public static final String PACKAGE = "_symboltable";
  public static final String LOG = SymbolTableGenerator.class.getSimpleName();

  public static final String SYMBOL_SUFFIX = "Symbol";
  public static final String EMPTY_SYMBOL_SUFFIX = "SymbolEMPTY";

  public static void generate(ASTMCGrammar astGrammar, GlobalScope globalScope, ASTCDCompilationUnit astCd,
      File outputPath, IterablePath handcodedPath) {

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
    final boolean skipSymbolTableGeneration = allSymbolDefiningRules.size() == 0;

    final GeneratorSetup setup = new GeneratorSetup(outputPath);
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    glex.setGlobalValue("stHelper", genHelper);
    glex.setGlobalValue("nameHelper", new Names());
    glex.setGlobalValue("skipSTGen", skipSymbolTableGeneration);
    setup.setGlex(glex);

    final GeneratorEngine generator = new GeneratorEngine(setup);

    generateModelingLanguage(genHelper, generator, grammarSymbol, handcodedPath, ruleNames);
    generateModelLoader(genHelper, generator, grammarSymbol, handcodedPath);

    if (!skipSymbolTableGeneration) {
      generateModelNameCalculator(genHelper, generator, grammarSymbol, handcodedPath, ruleNames);
      generateSymbolTableCreator(genHelper, generator, grammarSymbol, handcodedPath);

      for (MCRuleSymbol ruleSymbol : allSymbolDefiningRules) {
        generateSymbolAndScope(genHelper, generator, ruleSymbol, handcodedPath);
        generateSymbolKind(genHelper, generator, ruleSymbol, handcodedPath);

        generateSymbolReference(genHelper, generator, ruleSymbol, handcodedPath);

        generateResolvingFilters(genHelper, generator, ruleSymbol, handcodedPath);
      }
    }

    Log.debug("End symbol table generation for the grammar " + astGrammar.getName(), LOG);
  }

  private static void generateModelingLanguage(SymbolTableGeneratorHelper genHelper, GeneratorEngine generator,
      MCGrammarSymbol grammarSymbol, IterablePath handcodedPath, Collection<String> ruleNames) {
    final String className = getSimpleTypeNameToGenerate(getSimpleName(grammarSymbol.getName() + "Language"),
        genHelper.getTargetPackage(), handcodedPath);

    final Path filePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()), className + ".java");
    generator.generate("symboltable.ModelingLanguage", filePath, grammarSymbol.getAstNode().get(), className, ruleNames);
  }

  private static void generateModelLoader(SymbolTableGeneratorHelper genHelper,
      GeneratorEngine generator, MCGrammarSymbol grammarSymbol, IterablePath handcodedPath) {
    final String className = getSimpleTypeNameToGenerate(getSimpleName(grammarSymbol.getName() + "ModelLoader"),
        genHelper.getTargetPackage(), handcodedPath);

    final Path filePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()), className + ".java");

    generator.generate("symboltable.ModelLoader", filePath, grammarSymbol.getAstNode().get(), className);
  }

  private static void generateModelNameCalculator(SymbolTableGeneratorHelper genHelper,
      GeneratorEngine generator, MCGrammarSymbol grammarSymbol, IterablePath handcodedPath, Collection<String> ruleNames) {

    final String className = getSimpleTypeNameToGenerate(getSimpleName(grammarSymbol.getName() + "ModelNameCalculator"),
        genHelper.getTargetPackage(), handcodedPath);

    final Path filePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()), className + ".java");
    generator.generate("symboltable.ModelNameCalculator", filePath, grammarSymbol.getAstNode().get(), className, ruleNames);
  }

  private static void generateSymbolTableCreator(SymbolTableGeneratorHelper genHelper,
      GeneratorEngine generator, MCGrammarSymbol grammarSymbol, IterablePath handcodedPath) {
    final String className = getSimpleTypeNameToGenerate(getSimpleName(grammarSymbol.getName() + "SymbolTableCreator"),
        genHelper.getTargetPackage(), handcodedPath);

    final Path filePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()), className + ".java");

    List<CDSymbol> directSuperCds = genHelper.getDirectSuperCds(genHelper.getCd());
    generator.generate("symboltable.SymbolTableCreator", filePath, grammarSymbol.getAstNode().get(), className, directSuperCds);
  }

  private static void generateSymbolAndScope(SymbolTableGeneratorHelper genHelper, GeneratorEngine generator,
      MCRuleSymbol ruleSymbol, IterablePath handcodedPath) {
    final String className = getSimpleTypeNameToGenerate(getSimpleName(ruleSymbol.getName() + "Symbol"),
        genHelper.getTargetPackage(), handcodedPath);

    final Path filePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()), className + ".java");
    if (ruleSymbol.getAstNode().isPresent()) {
      if (!isScopeSpanningSymbol(genHelper, ruleSymbol)) {
        // normal symbol
        generateEmptySymbol(genHelper, generator, ruleSymbol, handcodedPath);
        if (!(INTERFACEORABSTRACTRULE.equals(ruleSymbol.getKindSymbolRule()))) {
          generator.generate("symboltable.Symbol", filePath, ruleSymbol.getAstNode().get(), className, ruleSymbol);
        }
      }
      else {
        // scope spanning symbol and its spanned scope
        generateEmptyScopeSpanningSymbol(genHelper, generator, ruleSymbol, handcodedPath);
        generator.generate("symboltable.ScopeSpanningSymbol", filePath, ruleSymbol.getAstNode().get(), className, ruleSymbol);
        generateScope(genHelper, generator, ruleSymbol, handcodedPath);
      }
    }
  }

  private static void generateEmptySymbol(SymbolTableGeneratorHelper genHelper, GeneratorEngine generator,
      MCRuleSymbol ruleSymbol, IterablePath handcodedPath) {
    // Interface and abstract rules both do not have any content. Hence, only the empty symbol interface must be generated.
    // In that case, the suffix is just "Symbol" instead of "SymbolEMPTY".
    final String suffix = INTERFACEORABSTRACTRULE.equals(ruleSymbol.getKindSymbolRule()) ? SYMBOL_SUFFIX : EMPTY_SYMBOL_SUFFIX;

    final String className = getSimpleTypeNameToGenerate(getSimpleName(ruleSymbol.getName() + suffix),
        genHelper.getTargetPackage(), handcodedPath);

    final Path filePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()), className + ".java");
    if (ruleSymbol.getAstNode().isPresent()) {
      generator.generate("symboltable.EmptySymbol", filePath, ruleSymbol.getAstNode().get(), className, ruleSymbol);
    }
  }

  private static void generateEmptyScopeSpanningSymbol(SymbolTableGeneratorHelper genHelper, GeneratorEngine generator,
      MCRuleSymbol ruleSymbol, IterablePath handcodedPath) {
    final String className = getSimpleTypeNameToGenerate(getSimpleName(ruleSymbol.getName() + EMPTY_SYMBOL_SUFFIX),
        genHelper.getTargetPackage(), handcodedPath);

    final Path filePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()), className + ".java");
    if (ruleSymbol.getAstNode().isPresent()) {
      final boolean isClass = ruleSymbol instanceof MCInterfaceOrAbstractRuleSymbol;
      generator.generate("symboltable.EmptyScopeSpanningSymbol", filePath, ruleSymbol.getAstNode().get(), className, ruleSymbol);
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

  private static void generateScope(SymbolTableGeneratorHelper genHelper, GeneratorEngine generator,
      MCRuleSymbol ruleSymbol, IterablePath handcodedPath) {
    final String className = ruleSymbol.getName() + "Scope";
    final String qualifiedClassName = getPackageName(genHelper.getTargetPackage(), "") + className;

    if(TransformationHelper.existsHandwrittenClass(handcodedPath, qualifiedClassName)) {
      // Scope classes are very simple and small. Hence, skip their generation
      // if handwritten class exists.
      return;
    }

    final Path filePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()), className + ".java");
    if (ruleSymbol.getAstNode().isPresent()) {
      generator.generate("symboltable.Scope", filePath, ruleSymbol.getAstNode().get(), className);
    }
  }

  private static void generateSymbolKind(SymbolTableGeneratorHelper genHelper, GeneratorEngine generator,
      MCRuleSymbol ruleSymbol, IterablePath handcodedPath) {
    final String className = ruleSymbol.getName() + "Kind";
    final String qualifiedClassName = getPackageName(genHelper.getTargetPackage(), "") + className;

    if(TransformationHelper.existsHandwrittenClass(handcodedPath, qualifiedClassName)) {
      // Symbol kind classes are very simple and small. Hence, skip their generation
      // if handwritten class exists.
      return;
    }

    final Path filePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()), className + ".java");
    if (ruleSymbol.getAstNode().isPresent()) {
      generator.generate("symboltable.SymbolKind", filePath, ruleSymbol.getAstNode().get(), ruleSymbol);
    }
  }

  private static void generateSymbolReference(SymbolTableGeneratorHelper genHelper,
      GeneratorEngine generator, MCRuleSymbol ruleSymbol, IterablePath handcodedPath) {
    final String className = getSimpleTypeNameToGenerate(getSimpleName(ruleSymbol.getName() + "SymbolReference"),
        genHelper.getTargetPackage(), handcodedPath);

    final Path filePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()), className + ".java");
    if (ruleSymbol.getAstNode().isPresent()) {
      final boolean isScopeSpanningSymbol = isScopeSpanningSymbol(genHelper, ruleSymbol);
      generator.generate("symboltable.SymbolReference", filePath, ruleSymbol.getAstNode().get(),
          className, ruleSymbol, isScopeSpanningSymbol);
    }
  }

  private static void generateResolvingFilters(SymbolTableGeneratorHelper genHelper, GeneratorEngine generator,
      MCRuleSymbol ruleSymbol, IterablePath handcodedPath) {
    final String className = ruleSymbol.getName() + "ResolvingFilter";
    final String qualifiedClassName = getPackageName(genHelper.getTargetPackage(), "") + className;

    if(TransformationHelper.existsHandwrittenClass(handcodedPath, qualifiedClassName)) {
      // ResolvingFilter classes are very simple and small. Hence, skip their generation
      // if handwritten class exists.
      return;
    }

    final Path filePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()), className + ".java");
    if (ruleSymbol.getAstNode().isPresent()) {
      generator.generate("symboltable.ResolvingFilter", filePath, ruleSymbol.getAstNode().get(), className, ruleSymbol.getName());
    }
  }

}
