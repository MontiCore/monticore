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
import de.monticore.io.paths.IterablePath;
import de.monticore.languages.grammar.MCRuleSymbol;
import de.se_rwth.commons.Names;

/**
 * @author Pedram Mir Seyed Nazari
 */
public class CommonSymbolReferenceGenerator implements SymbolReferenceGenerator {

  private final SymbolTableGeneratorHelper genHelper;
  private final IterablePath handCodedPath;
  private final GeneratorEngine genEngine;

  public CommonSymbolReferenceGenerator(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper, IterablePath handCodedPath) {
    this.genEngine = genEngine;
    this.genHelper = genHelper;
    this.handCodedPath = handCodedPath;
  }

  @Override
  public void generate(MCRuleSymbol ruleSymbol, boolean isScopeSpanningSymbol) {
    final String className = getSimpleTypeNameToGenerate(getSimpleName(ruleSymbol.getName() + "SymbolReference"),
        genHelper.getTargetPackage(), handCodedPath);

    final Path filePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()), className + ".java");
    if (ruleSymbol.getAstNode().isPresent()) {
      genEngine.generate("symboltable.SymbolReference", filePath, ruleSymbol.getAstNode().get(),
          className, ruleSymbol, isScopeSpanningSymbol);
    }
  }
}
