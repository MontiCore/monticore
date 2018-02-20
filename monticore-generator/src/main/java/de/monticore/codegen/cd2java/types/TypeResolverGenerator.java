/* (c) https://github.com/MontiCore/monticore */

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
    final GeneratorSetup setup = new GeneratorSetup();
    setup.setOutputDirectory(outputDirectory);
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
