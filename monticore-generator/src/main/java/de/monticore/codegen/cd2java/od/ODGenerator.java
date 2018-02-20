/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.cd2java.od;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.ast.AstGeneratorHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.symboltable.GlobalScope;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;


public class ODGenerator {
  private final static String LOGGER_NAME = ODGenerator.class.getName();
  
  /**
   * Generates the different visitor default implementations for the given class
   * diagram.
   */
  public static void generate(GlobalExtensionManagement glex, GlobalScope globalScope,
      ASTCDCompilationUnit astClassDiagram,
      File outputDirectory) {
    final GeneratorSetup setup = new GeneratorSetup();
    setup.setOutputDirectory(outputDirectory);
    GeneratorHelper odHelper = new GeneratorHelper(astClassDiagram, globalScope);
    glex.setGlobalValue("odHelper", odHelper);
    setup.setGlex(glex);
    final GeneratorEngine generator = new GeneratorEngine(setup);
    final String diagramName = astClassDiagram.getCDDefinition().getName();
    final CDSymbol cd = odHelper.getCd();
    
    final String astPackage = GeneratorHelper.getPackageName(odHelper.getPackageName(),
        AstGeneratorHelper.getAstPackageSuffix());
    
    final String visitorPackage = odHelper.getPackageName() + "._od";
    String path = Names.getPathFromPackage(visitorPackage);
    
    // simple visitor for creating the OD
    final Path simpleVisitorFilePath = Paths.get(path, diagramName + "2OD.java");
    generator.generate("od.DiagramVisitor", simpleVisitorFilePath, astClassDiagram,
        astClassDiagram.getCDDefinition(), astPackage, cd);
    Log.trace(LOGGER_NAME, "Generated visitor for the creating the OD to the diagram: " + diagramName);
    
  }
  
  private ODGenerator() {
    // noninstantiable
  }
}
