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

package de.monticore.codegen.cd2java.ast;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.visitor.VisitorGeneratorHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.IterablePath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDEnum;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.se_rwth.commons.JavaNamesHelper;
import de.se_rwth.commons.Names;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 */
public class AstGenerator {
  
  private static final String JAVA_EXTENSION = ".java";
  
  /**
   * Generates ast files for the given class diagram AST
   * 
   * @param glex - object for managing hook points, features and global
   * variables
   * @param c 
   * @param astClassDiagram - class diagram AST
   * @param templateName - the qualified name of the start template
   * @param outputDirectory - target directory
   */
  public static void generate(GlobalExtensionManagement glex, GlobalScope globalScope, ASTCDCompilationUnit astClassDiagram,
      File outputDirectory, IterablePath templatePath, boolean emfCompatible) {
    final String diagramName = astClassDiagram.getCDDefinition().getName();
    final GeneratorSetup setup = new GeneratorSetup(outputDirectory);
    setup.setModelName(diagramName);
    setup.setAdditionalTemplatePaths(templatePath.getPaths().stream().map(Path::toFile).collect(Collectors.toList()));
    AstGeneratorHelper astHelper = GeneratorHelper.createGeneratorHelper(astClassDiagram, globalScope, emfCompatible);
    glex.setGlobalValue("astHelper", astHelper);
    glex.setGlobalValue("javaNameHelper", new JavaNamesHelper());
    glex.setGlobalValue("nameHelper", new Names());
    setup.setGlex(glex);
    
    final GeneratorEngine generator = new GeneratorEngine(setup);
    final String astPackage = astHelper.getAstPackage();
    final String visitorPackage = AstGeneratorHelper.getPackageName(astHelper.getPackageName(),
        VisitorGeneratorHelper.getVisitorPackageSuffix());
    
    for (ASTCDClass clazz : astClassDiagram.getCDDefinition().getCDClasses()) {
      final Path filePath = Paths.get(Names.getPathFromPackage(astPackage),
          Names.getSimpleName(clazz.getName()) + JAVA_EXTENSION);
      if (astHelper.isAstClass(clazz)) {
        generator.generate("ast.AstClass", filePath, clazz, clazz, astHelper.getASTBuilder(clazz));
      }
      else if (!AstGeneratorHelper.isBuilderClass(astClassDiagram.getCDDefinition(), clazz)) {
        generator.generate("ast.Class", filePath, clazz);
      }
    }
    
    for (ASTCDInterface interf : astClassDiagram.getCDDefinition().getCDInterfaces()) {
      final Path filePath = Paths.get(Names.getPathFromPackage(astPackage),
          Names.getSimpleName(interf.getName()) + JAVA_EXTENSION);
      generator.generate("ast.AstInterface", filePath, interf, visitorPackage,
          VisitorGeneratorHelper.getVisitorType(diagramName));
    }
    
    for (ASTCDEnum enm : astClassDiagram.getCDDefinition().getCDEnums()) {
      final Path filePath = Paths.get(Names.getPathFromPackage(astPackage),
          Names.getSimpleName(enm.getName()) + JAVA_EXTENSION);
      generator.generate("ast.AstEnum", filePath, enm);
    }
    
  }
  
  private AstGenerator() {
    // noninstantiable
  }
}
