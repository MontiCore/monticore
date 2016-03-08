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
import java.util.List;

import de.monticore.generating.GeneratorEngine;
import de.monticore.io.paths.IterablePath;
import de.monticore.languages.grammar.MCGrammarSymbol;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.se_rwth.commons.Names;

/**
 * @author Pedram Mir Seyed Nazari
 */
public class CommonSymbolTableCreatorGenerator implements SymbolTableCreatorGenerator {

  @Override
  public void generate(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper,
      IterablePath handCodedPath, MCGrammarSymbol grammarSymbol) {
    final String className = getSimpleTypeNameToGenerate(getSimpleName(grammarSymbol.getFullName() + "SymbolTableCreator"),
        genHelper.getTargetPackage(), handCodedPath);

    final Path filePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()), className + ".java");

    List<CDSymbol> directSuperCds = genHelper.getDirectSuperCds(genHelper.getCd());
    genEngine.generate("symboltable.SymbolTableCreator", filePath, grammarSymbol.getAstNode().get(), className, directSuperCds);
  }
}
