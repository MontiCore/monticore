/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.cd2java.visitor;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import de.monticore.codegen.cd2java.ast.AstGeneratorHelper;
import de.monticore.codegen.symboltable.SymbolTableGeneratorHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.symboltable.MCProdSymbol;
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
    final GeneratorSetup setup = new GeneratorSetup();
    setup.setOutputDirectory(outputDirectory);
    VisitorGeneratorHelper visitorHelper = new VisitorGeneratorHelper(astClassDiagram, globalScope);
    glex.setGlobalValue("visitorHelper", visitorHelper);
    setup.setGlex(glex);
    final GeneratorEngine generator = new GeneratorEngine(setup);
    final String diagramName = astClassDiagram.getCDDefinition().getName();
    final CDSymbol cd = visitorHelper.getCd();
    
    final String astPackage = VisitorGeneratorHelper.getPackageName(visitorHelper.getPackageName(),
        AstGeneratorHelper.getAstPackageSuffix());
    final String symbolTablePackage = VisitorGeneratorHelper.getPackageName(visitorHelper.getPackageName(),
        AstGeneratorHelper.getSymbolTablePackageSuffix());
    
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
    
    // symbol visitor interface
    Collection<MCProdSymbol> symbols  = new LinkedHashSet<>();
    Object stHelperObj = glex.getGlobalVar("stHelper");
    if (stHelperObj != null && stHelperObj instanceof SymbolTableGeneratorHelper) {
      SymbolTableGeneratorHelper stHelper = (SymbolTableGeneratorHelper) stHelperObj;
      symbols = stHelper.getAllSymbolDefiningRules();
    }
    final Path symbolVisitorFilePath = Paths.get(path, visitorHelper.getSymbolVisitorType() + ".java");
    generator.generate("visitor.SymbolVisitor", symbolVisitorFilePath, astClassDiagram,
        astClassDiagram.getCDDefinition(), symbolTablePackage, cd, symbols);
    Log.trace(LOGGER_NAME, "Generated symbol visitor for the diagram: " + diagramName);
    
    // scope visitor interface
    boolean existsST = false;
    Set<String> superScopeVisitors = new HashSet<>();
    if (stHelperObj != null && stHelperObj instanceof SymbolTableGeneratorHelper) {
      SymbolTableGeneratorHelper stHelper = (SymbolTableGeneratorHelper) stHelperObj;
      if (stHelper.getGrammarSymbol().getStartProd().isPresent()) {
        existsST = true;
      }
      // super scope visitors
      for (CDSymbol cdSymbol : stHelper.getDirectSuperCds(stHelper.getCd())) {
        String qualifiedScopeVisitorName = stHelper.getQualifiedScopeVisitorType(cdSymbol);
        if (!qualifiedScopeVisitorName.isEmpty() && !cdSymbol.equals(stHelper.getCd())) {
          superScopeVisitors.add(qualifiedScopeVisitorName);
        }
      }
    }
    final Path scopeVisitorFilePath = Paths.get(path, visitorHelper.getScopeVisitorType() + ".java");
    generator.generate("visitor.ScopeVisitor", scopeVisitorFilePath, astClassDiagram,
        astClassDiagram.getCDDefinition(), symbolTablePackage, cd, existsST, superScopeVisitors);
    Log.trace(LOGGER_NAME, "Generated scope visitor for the diagram: " + diagramName);
  }
  
  private VisitorGenerator() {
    // noninstantiable
  }
}
