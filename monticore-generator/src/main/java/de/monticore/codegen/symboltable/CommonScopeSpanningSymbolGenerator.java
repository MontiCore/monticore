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

import static de.monticore.codegen.GeneratorHelper.getPackageName;
import static de.monticore.codegen.GeneratorHelper.getSimpleTypeNameToGenerate;
import static de.se_rwth.commons.Names.getSimpleName;

import java.nio.file.Path;
import java.nio.file.Paths;

import de.monticore.codegen.mc2cd.EssentialTransformationHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.io.paths.IterablePath;
import de.se_rwth.commons.Names;

/**
 * @author Pedram Mir Seyed Nazari
 */
public class CommonScopeSpanningSymbolGenerator implements ScopeSpanningSymbolGenerator {

  public static final String EMPTY_SYMBOL_SUFFIX = "SymbolEMPTY";

  public void generate(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper,
      IterablePath handCodedPath, MCProdSymbol ruleSymbol) {
    final String className = getSimpleTypeNameToGenerate(getSimpleName(ruleSymbol.getName() + "Symbol"),
        genHelper.getTargetPackage(), handCodedPath);

    final Path filePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()), className + ".java");

    generateEmpty(genEngine, genHelper, handCodedPath, ruleSymbol);
    genEngine.generate("symboltable.ScopeSpanningSymbol", filePath, ruleSymbol.getAstNode().get(), className, ruleSymbol);
    generateScope(genEngine, genHelper, handCodedPath, ruleSymbol);
  }

  protected void generateEmpty(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper,
      IterablePath handCodedPath, MCProdSymbol ruleSymbol) {
    final String className = getSimpleTypeNameToGenerate(getSimpleName(ruleSymbol.getName() + EMPTY_SYMBOL_SUFFIX),
        genHelper.getTargetPackage(), handCodedPath);

    final Path filePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()), className + ".java");
    if (ruleSymbol.getAstNode().isPresent()) {
      genEngine.generate("symboltable.EmptyScopeSpanningSymbol", filePath, ruleSymbol.getAstNode().get(), className, ruleSymbol);
    }
  }

  protected void generateScope(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper, IterablePath handCodedPath,
      MCProdSymbol ruleSymbol) {
    final String className = ruleSymbol.getName() + "Scope";
    final String qualifiedClassName = getPackageName(genHelper.getTargetPackage(), "") + className;

    if(EssentialTransformationHelper.existsHandwrittenClass(handCodedPath, qualifiedClassName)) {
      // Scope classes are very simple and small. Hence, skip their generation
      // if handwritten class exists.
      return;
    }

    final Path filePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()), className + ".java");
    if (ruleSymbol.getAstNode().isPresent()) {
      genEngine.generate("symboltable.Scope", filePath, ruleSymbol.getAstNode().get(), className);
    }
  }

}
