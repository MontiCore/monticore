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
import static de.se_rwth.commons.Names.getSimpleName;

import java.nio.file.Path;
import java.nio.file.Paths;

import de.monticore.generating.GeneratorEngine;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.io.paths.IterablePath;
import de.se_rwth.commons.Names;

/**
 * @author Pedram Mir Seyed Nazari
 */
public class CommonSymbolGenerator implements SymbolGenerator {

  public static final String SYMBOL_SUFFIX = "Symbol";
  public static final String EMPTY_SYMBOL_SUFFIX = "SymbolEMPTY";

  @Override
  public void generate(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper,
      IterablePath handCodedPath, MCProdSymbol ruleSymbol) {
    final String className = getSimpleTypeNameToGenerate(getSimpleName(ruleSymbol.getName() + "Symbol"),
        genHelper.getTargetPackage(), handCodedPath);

    final Path filePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()), className + ".java");

    generateEmpty(genEngine, genHelper, handCodedPath, ruleSymbol);
    if (!ruleSymbol.isAbstract() && !ruleSymbol.isInterface()) {
      genEngine.generate("symboltable.Symbol", filePath, ruleSymbol.getAstNode().get(), className, ruleSymbol);
    }
  }

  protected void generateEmpty(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper,
      IterablePath handCodedPath, MCProdSymbol ruleSymbol) {
    // Interface and abstract rules both do not have any content. Hence, only the empty symbol interface must be generated.
    // In that case, the suffix is just "Symbol" instead of "SymbolEMPTY".
    final String suffix = ruleSymbol.isAbstract() || ruleSymbol.isInterface()
        ? SYMBOL_SUFFIX : EMPTY_SYMBOL_SUFFIX;

    final String className = getSimpleTypeNameToGenerate(getSimpleName(ruleSymbol.getName() + suffix),
        genHelper.getTargetPackage(), handCodedPath);

    final Path filePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()), className + ".java");
    if (ruleSymbol.getAstNode().isPresent()) {
      genEngine.generate("symboltable.EmptySymbol", filePath, ruleSymbol.getAstNode().get(), className, ruleSymbol);
    }
  }
}
