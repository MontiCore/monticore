/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.cd2java.visitor;

import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.codegen.cd2java.ast.AstGeneratorHelper;
import de.monticore.codegen.symboltable.SymbolTableGeneratorHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.io.paths.IterablePath;
import de.monticore.symboltable.GlobalScope;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static de.monticore.codegen.GeneratorHelper.getSimpleTypeNameToGenerate;
import static de.se_rwth.commons.Names.getSimpleName;

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
                              ASTCDCompilationUnit astClassDiagram, File outputDirectory,
                              IterablePath handcodedPath) {
    final GeneratorSetup setup = new GeneratorSetup();
    setup.setOutputDirectory(outputDirectory);
    VisitorGeneratorHelper visitorHelper = new VisitorGeneratorHelper(astClassDiagram, globalScope);
    glex.setGlobalValue("visitorHelper", visitorHelper);
    setup.setGlex(glex);
    final GeneratorEngine generator = new GeneratorEngine(setup);
    final String diagramName = astClassDiagram.getCDDefinition().getName();
    final CDDefinitionSymbol cd = visitorHelper.getCd();
    
    final String astPackage = VisitorGeneratorHelper.getPackageName(visitorHelper.getPackageName(),
        AstGeneratorHelper.getAstPackageSuffix());
    final String symbolTablePackage = VisitorGeneratorHelper.getPackageName(visitorHelper.getPackageName(),
        AstGeneratorHelper.getSymbolTablePackageSuffix());
    
    final String visitorPackage = visitorHelper.getVisitorPackage();
    String path = Names.getPathFromPackage(visitorPackage);
    
    // simple visitor interface
    String simpleVisitorName = getSimpleTypeNameToGenerate(getSimpleName(visitorHelper.getVisitorType()),
        visitorHelper.getVisitorPackage(), handcodedPath);
    final Path simpleVisitorFilePath = Paths.get(path, simpleVisitorName + ".java");
    generator.generate("visitor.SimpleVisitor", simpleVisitorFilePath, astClassDiagram,
        simpleVisitorName, astClassDiagram.getCDDefinition(), astPackage, cd);
    Log.trace(LOGGER_NAME, "Generated simple visitor for the diagram: " + diagramName);
    
    // inheritance visitor interface
    String inheritanceVisitorName = getSimpleTypeNameToGenerate(getSimpleName(visitorHelper.getInheritanceVisitorType()),
        visitorHelper.getVisitorPackage(), handcodedPath);
    final Path inheritanceVisitorFilePath = Paths
        .get(path, inheritanceVisitorName + ".java");
    generator.generate("visitor.InheritanceVisitor", inheritanceVisitorFilePath, astClassDiagram,
        inheritanceVisitorName, astClassDiagram.getCDDefinition(), astPackage, cd);
    Log.trace(LOGGER_NAME, "Generated inheritance visitor for the diagram: " + diagramName);
    
    // parent aware visitor interface
    String parentAwareVisitorName = getSimpleTypeNameToGenerate(getSimpleName(diagramName + "ParentAwareVisitor"),
        visitorHelper.getVisitorPackage(), handcodedPath);
    final Path parentAwareVisitorFilePath = Paths
        .get(path, parentAwareVisitorName + ".java");
    generator.generate("visitor.ParentAwareVisitor", parentAwareVisitorFilePath, astClassDiagram,
        parentAwareVisitorName, astClassDiagram.getCDDefinition(), astPackage, cd);
    Log.trace(LOGGER_NAME, "Generated basic parent aware visitor for the diagram: " + diagramName);
    
    List<CDDefinitionSymbol> allCds = visitorHelper.getAllCds(cd);
    
    // common delegator visitor
    String commonDelegatorVisitorName = getSimpleTypeNameToGenerate(getSimpleName(visitorHelper.getDelegatorVisitorType()),
        visitorHelper.getVisitorPackage(), handcodedPath);
    final Path commonDelegatorVisitorFilePath = Paths.get(path,
        commonDelegatorVisitorName + ".java");
    generator.generate("visitor.DelegatorVisitor", commonDelegatorVisitorFilePath,
        astClassDiagram, commonDelegatorVisitorName, astClassDiagram.getCDDefinition(),
        astPackage, allCds);
    Log.trace(LOGGER_NAME, "Generated delegator visitor for the diagram: " + diagramName);
    
    // symbol visitor interface
    Collection<MCProdSymbol> symbols  = new LinkedHashSet<>();
    Object stHelperObj = glex.getGlobalVar("stHelper");
    if (stHelperObj != null && stHelperObj instanceof SymbolTableGeneratorHelper) {
      SymbolTableGeneratorHelper stHelper = (SymbolTableGeneratorHelper) stHelperObj;
      symbols = stHelper.getAllSymbolDefiningRules();
    }
    String symbolVisitorName = getSimpleTypeNameToGenerate(getSimpleName(visitorHelper.getSymbolVisitorType()),
        visitorHelper.getVisitorPackage(), handcodedPath);
    final Path symbolVisitorFilePath = Paths.get(path, symbolVisitorName + ".java");
    generator.generate("visitor.SymbolVisitor", symbolVisitorFilePath, astClassDiagram,
        symbolVisitorName, astClassDiagram.getCDDefinition(), symbolTablePackage, cd, symbols);
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
      for (CDDefinitionSymbol cdSymbol : stHelper.getDirectSuperCds(stHelper.getCd())) {
        String qualifiedScopeVisitorName = stHelper.getQualifiedScopeVisitorType(cdSymbol);
        if (!qualifiedScopeVisitorName.isEmpty() && !cdSymbol.equals(stHelper.getCd())) {
          superScopeVisitors.add(qualifiedScopeVisitorName);
        }
      }
    }
    String scopeVisitorName = getSimpleTypeNameToGenerate(getSimpleName(visitorHelper.getScopeVisitorType()),
        visitorHelper.getVisitorPackage(), handcodedPath);
    final Path scopeVisitorFilePath = Paths.get(path, scopeVisitorName + ".java");
    generator.generate("visitor.ScopeVisitor", scopeVisitorFilePath, astClassDiagram,
        scopeVisitorName, astClassDiagram.getCDDefinition(), symbolTablePackage, cd,
        existsST, superScopeVisitors);
    Log.trace(LOGGER_NAME, "Generated scope visitor for the diagram: " + diagramName);
  }
  
  private VisitorGenerator() {
    // noninstantiable
  }
}
