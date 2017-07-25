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

package de.monticore.codegen.cd2java.visitor;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import de.monticore.codegen.cd2java.ast.AstGeneratorHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.symboltable.GlobalScope;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

/**
 * Generates the visitor infrastructure consisting of different default visitor
 * implementations and delegation concepts for composing visitors across
 * languages.
 *
 * @author Robert Heim
 */
public class VisitorGenerator {
  private final static String LOGGER_NAME = VisitorGenerator.class.getName();
  
  /**
   * Generates the different visitor default implementations for the given class
   * diagram.
   */
  public static void generate(GlobalExtensionManagement glex, GlobalScope globalScope,
      ASTCDCompilationUnit astClassDiagram,
      File outputDirectory) {
    final GeneratorSetup setup = new GeneratorSetup(outputDirectory);
    VisitorGeneratorHelper visitorHelper = new VisitorGeneratorHelper(astClassDiagram, globalScope);
    glex.setGlobalValue("visitorHelper", visitorHelper);
    setup.setGlex(glex);
    final GeneratorEngine generator = new GeneratorEngine(setup);
    final String diagramName = astClassDiagram.getCDDefinition().getName();
    final CDSymbol cd = visitorHelper.getCd();
    
    final String astPackage = VisitorGeneratorHelper.getPackageName(visitorHelper.getPackageName(),
        AstGeneratorHelper.getAstPackageSuffix());
    
    final String visitorPackage = visitorHelper.getVisitorPackage();
    String path = Names.getPathFromPackage(visitorPackage);
    
    // simple visitor interface
    final Path simpleVisitorFilePath = Paths.get(path, visitorHelper.getVisitorType() + ".java");
    generator.generate("visitor.SimpleVisitor", simpleVisitorFilePath, astClassDiagram,
        astClassDiagram.getCDDefinition(), astPackage, cd);
    Log.trace(LOGGER_NAME, "Generated simple visitor for the diagram: " + diagramName);
    
    // inheritance visitor interface
    final Path inheritanceVisitorFilePath = Paths
        .get(path, visitorHelper.getInheritanceVisitorType() + ".java");
    generator.generate("visitor.InheritanceVisitor", inheritanceVisitorFilePath, astClassDiagram,
        astClassDiagram.getCDDefinition(), astPackage, cd);
    Log.trace(LOGGER_NAME, "Generated inheritance visitor for the diagram: " + diagramName);
    
    // parent aware visitor interface
    final Path parentAwareVisitorFilePath = Paths
        .get(path, diagramName + "ParentAwareVisitor.java");
    generator.generate("visitor.ParentAwareVisitor", parentAwareVisitorFilePath, astClassDiagram,
        astClassDiagram.getCDDefinition(), astPackage, cd);
    Log.trace(LOGGER_NAME, "Generated basic parent aware visitor for the diagram: " + diagramName);
    
    List<CDSymbol> allCds = visitorHelper.getAllCds(cd);
    
    // common delegator visitor
    final Path commonDelegatorVisitorFilePath = Paths.get(path,
        visitorHelper.getDelegatorVisitorType() + ".java");
    generator.generate("visitor.DelegatorVisitor", commonDelegatorVisitorFilePath,
        astClassDiagram, astClassDiagram.getCDDefinition(), astPackage, allCds);
    Log.trace(LOGGER_NAME, "Generated delegator visitor for the diagram: " + diagramName);
  }
  
  private VisitorGenerator() {
    // noninstantiable
  }
}
