package de.monticore.codegen.cd2java;

import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.umlcd4a.CD4AnalysisLanguage;
import de.monticore.umlcd4a.CD4AnalysisModelLoader;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._parser.CD4AnalysisParser;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Optional;

import static org.junit.Assert.fail;

public abstract class DecoratorTestCase {

  private static final String MODEL_PATH = "src/test/resources";

  public ASTCDCompilationUnit parse(String... names) {
    String qualifiedName = String.join(".", names);
    CD4AnalysisLanguage cd4AnalysisLanguage = new CD4AnalysisLanguage();
    ResolvingConfiguration resolvingConfiguration = new ResolvingConfiguration();
    resolvingConfiguration.addDefaultFilters(cd4AnalysisLanguage.getResolvingFilters());
    CD4AnalysisModelLoader modelLoader = new CD4AnalysisModelLoader(cd4AnalysisLanguage);
    ModelPath modelPath = new ModelPath(Paths.get(MODEL_PATH));
    GlobalScope globalScope = new GlobalScope(modelPath, cd4AnalysisLanguage);
    Collection<ASTCDCompilationUnit> ast = modelLoader.loadModelsIntoScope(qualifiedName, modelPath, globalScope, resolvingConfiguration);
    if (ast.isEmpty())
      fail(String.format("Failed to load model '%s'", qualifiedName));
    if (ast.size() > 1)
      fail(String.format("Multiple models loaded model '%s'", qualifiedName));
    return ast.iterator().next();
  }
}
