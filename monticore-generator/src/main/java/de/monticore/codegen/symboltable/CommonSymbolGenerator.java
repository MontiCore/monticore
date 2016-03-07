/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2016, MontiCore, All rights reserved.
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

import static de.monticore.codegen.GeneratorHelper.getSimpleTypeNameToGenerate;
import static de.monticore.languages.grammar.MCRuleSymbol.KindSymbolRule.INTERFACEORABSTRACTRULE;
import static de.se_rwth.commons.Names.getSimpleName;

import java.nio.file.Path;
import java.nio.file.Paths;

import de.monticore.generating.GeneratorEngine;
import de.monticore.io.paths.IterablePath;
import de.monticore.languages.grammar.MCRuleSymbol;
import de.se_rwth.commons.Names;

/**
 * @author Pedram Mir Seyed Nazari
 */
public class CommonSymbolGenerator implements SymbolGenerator {

  public static final String SYMBOL_SUFFIX = "Symbol";
  public static final String EMPTY_SYMBOL_SUFFIX = "SymbolEMPTY";

  private final SymbolTableGeneratorHelper genHelper;
  private final IterablePath handCodedPath;
  private final GeneratorEngine genEngine;

  public CommonSymbolGenerator(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper, IterablePath handCodedPath) {
    this.genEngine = genEngine;
    this.genHelper = genHelper;
    this.handCodedPath = handCodedPath;
  }

  @Override
  public void generate(MCRuleSymbol ruleSymbol) {
    final String className = getSimpleTypeNameToGenerate(getSimpleName(ruleSymbol.getName() + "Symbol"),
        genHelper.getTargetPackage(), handCodedPath);

    final Path filePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()), className + ".java");

    generateEmptySymbol(ruleSymbol);
    if (!(INTERFACEORABSTRACTRULE.equals(ruleSymbol.getKindSymbolRule()))) {
      genEngine.generate("symboltable.Symbol", filePath, ruleSymbol.getAstNode().get(), className, ruleSymbol);
    }
  }

  protected void generateEmptySymbol(MCRuleSymbol ruleSymbol) {
    // Interface and abstract rules both do not have any content. Hence, only the empty symbol interface must be generated.
    // In that case, the suffix is just "Symbol" instead of "SymbolEMPTY".
    final String suffix = INTERFACEORABSTRACTRULE.equals(ruleSymbol.getKindSymbolRule())
        ? SYMBOL_SUFFIX : EMPTY_SYMBOL_SUFFIX;

    final String className = getSimpleTypeNameToGenerate(getSimpleName(ruleSymbol.getName() + suffix),
        genHelper.getTargetPackage(), handCodedPath);

    final Path filePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()), className + ".java");
    if (ruleSymbol.getAstNode().isPresent()) {
      genEngine.generate("symboltable.EmptySymbol", filePath, ruleSymbol.getAstNode().get(), className, ruleSymbol);
    }
  }
}
