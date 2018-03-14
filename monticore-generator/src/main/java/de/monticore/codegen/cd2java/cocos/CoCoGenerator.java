/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.cd2java.cocos;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import de.monticore.codegen.cd2java.ast.AstGeneratorHelper;
import de.monticore.codegen.cd2java.visitor.VisitorGenerator;
import de.monticore.codegen.cd2java.visitor.VisitorGeneratorHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.symboltable.GlobalScope;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.se_rwth.commons.Names;

/**
 * Generates the infrastructure for context conditions: For the different ast
 * node types a corresponding coco-interface is generated. Their implementations
 * can be registered at the generated CoCoChecker (basically a visitor that
 * executes the registered cocos at the corresponding ast nodes).
 *
 * @author Robert Heim
 */
public class CoCoGenerator {
  
  /**
   * Generates the infrastructure for CoCos based on Visitor and the double
   * dispatch mechanism of {@link VisitorGenerator}.
   */
  public static void generate(GlobalExtensionManagement glex,
      GlobalScope globalScope,
      ASTCDCompilationUnit astClassDiagram, File outputDirectory) {
    final GeneratorSetup setup = new GeneratorSetup();
    setup.setOutputDirectory(outputDirectory);
    CoCoGeneratorHelper coCoHelper = new CoCoGeneratorHelper(astClassDiagram, globalScope);
    glex.setGlobalValue("coCoHelper", coCoHelper);
    setup.setGlex(glex);
    
    final GeneratorEngine generator = new GeneratorEngine(setup);
    final String diagramName = astClassDiagram.getCDDefinition().getName();
    final String cocosPackage = coCoHelper.getCoCoPackage();
    final String astPackage = CoCoGeneratorHelper.getPackageName(coCoHelper.getPackageName(),
        AstGeneratorHelper.getAstPackageSuffix());
    final String visitorPackage = CoCoGeneratorHelper.getPackageName(coCoHelper.getPackageName(),
        VisitorGeneratorHelper.getVisitorPackageSuffix());
    final CDSymbol cd = coCoHelper.getCd();
    
    // TODO generate interfaces using cd instead of ast? Would prevent duplicate
    // code
    
    // concrete coco interfaces for AST classes
    for (ASTCDClass clazz : astClassDiagram.getCDDefinition().getCDClassList()) {
      if (coCoHelper.isAstClass(clazz)) {
        final Path cocoFilePath = Paths.get(Names.getPathFromPackage(cocosPackage),
            diagramName + CoCoGeneratorHelper.getPlainName(clazz) + "CoCo.java");
        generator.generate("cocos.CoCoInterface", cocoFilePath, clazz, astPackage);
      }
    }
    // concrete coco interfaces for AST interfaces classes
    for (ASTCDInterface interf : astClassDiagram.getCDDefinition().getCDInterfaceList()) {
      final Path cocoFilePath = Paths.get(Names.getPathFromPackage(cocosPackage),
          diagramName + CoCoGeneratorHelper.getPlainName(interf) + "CoCo.java");
      generator.generate("cocos.CoCoInterface", cocoFilePath, interf, astPackage);
    }
    
    Collection<CDSymbol> allCds = coCoHelper.getAllCds(cd);
    
    // coco checker
    final String checkerType = diagramName + "CoCoChecker";
    final Path cocoCheckerFilePath = Paths.get(Names.getPathFromPackage(cocosPackage), diagramName
        + "CoCoChecker.java");
    generator.generate("cocos.CoCoChecker", cocoCheckerFilePath, astClassDiagram,
        astClassDiagram.getCDDefinition(), astPackage, checkerType, visitorPackage, cd, allCds);
  }
  
}
