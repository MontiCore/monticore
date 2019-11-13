/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.cd2java.od;

import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsGlobalScope;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;


public class ODGenerator {
  private final static String LOGGER_NAME = ODGenerator.class.getName();
  
  /**
   * Generates the different visitor default implementations for the given class
   * diagram.
   */
  public static void generate(GlobalExtensionManagement glex, ASTCDCompilationUnit astClassDiagram,
                              ASTMCGrammar grammar,
                              CD4AnalysisGlobalScope cdScope,
                              Grammar_WithConceptsGlobalScope mcScope,
                              File outputDirectory) {
    final GeneratorSetup setup = new GeneratorSetup();
    setup.setOutputDirectory(outputDirectory);
    GeneratorHelper odHelper = new GeneratorHelper(astClassDiagram, cdScope);
    glex.setGlobalValue("odHelper", odHelper);
    setup.setGlex(glex);
    final GeneratorEngine generator = new GeneratorEngine(setup);
    final String diagramName = astClassDiagram.getCDDefinition().getName();
    final CDDefinitionSymbol cd = odHelper.getCd();
    final String astPackage = GeneratorHelper.getPackageName(odHelper.getPackageName(),
            GeneratorHelper.AST_PACKAGE_SUFFIX);
    Optional<MCGrammarSymbol> grammarSymbol = grammar.getSymbolOpt();
    if (!grammarSymbol.isPresent()) {
      return;
    }
    
    final String visitorPackage = odHelper.getPackageName() + "._od";
    String path = Names.getPathFromPackage(visitorPackage);
    
    // simple visitor for creating the OD
    final Path simpleVisitorFilePath = Paths.get(path, diagramName + "2OD.java");
    generator.generate("od.DiagramVisitor", simpleVisitorFilePath, astClassDiagram,
        astClassDiagram.getCDDefinition(), astPackage, cd, grammarSymbol.get());
    Log.trace(LOGGER_NAME, "Generated visitor for the creating the OD to the diagram: " + diagramName);
    
  }
  
  private ODGenerator() {
    // noninstantiable
  }
}
