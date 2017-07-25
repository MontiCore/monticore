/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
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

package de.monticore.codegen.cd2java.types;

import de.monticore.codegen.cd2java.ast.AstGeneratorHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.symboltable.GlobalScope;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
/**
 *  on 06.11.2016.
 */
public class TypeResolverGenerator {
  private final static String LOGGER_NAME = TypeResolverGenerator.class.getName();

  /**
   * Generates the type resolver components, generic type result handler, generic type resolver
   * for the given class
   * diagram.
   */
  public static void generate(GlobalExtensionManagement glex, GlobalScope globalScope,
      ASTCDCompilationUnit astClassDiagram,
      File outputDirectory) {
    final GeneratorSetup setup = new GeneratorSetup(outputDirectory);
    TypeResolverGeneratorHelper typeResolverHelper = new TypeResolverGeneratorHelper(astClassDiagram, globalScope);
    glex.setGlobalValue("typeResolverHelper", typeResolverHelper);
    setup.setGlex(glex);
    final GeneratorEngine generator = new GeneratorEngine(setup);
    final String diagramName = astClassDiagram.getCDDefinition().getName();
    final CDSymbol cd = typeResolverHelper.getCd();

    final String astPackage = TypeResolverGeneratorHelper.getPackageName(typeResolverHelper.getPackageName(),
        AstGeneratorHelper.getAstPackageSuffix());

    final String typeResolverPackage = typeResolverHelper.getTypeResolverPackage();
    String path = Names.getPathFromPackage(typeResolverPackage);
    // type resolver
    final Path resolverPath = Paths.get(path, typeResolverHelper.getTypeResolver() + ".java");
    generator.generate("types.TypeResolver", resolverPath, astClassDiagram,
        astClassDiagram.getCDDefinition(), astPackage, cd);
    Log.trace(LOGGER_NAME, "Generated  type resolver for the diagram: " + diagramName);

  }

  private TypeResolverGenerator() {
    // noninstantiable
  }
}
