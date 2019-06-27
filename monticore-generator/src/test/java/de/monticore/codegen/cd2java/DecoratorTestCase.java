package de.monticore.codegen.cd2java;

import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisLanguage;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisModelLoader;
import de.monticore.io.paths.ModelPath;

import java.nio.file.Paths;
import java.util.Collection;

import static org.junit.Assert.fail;

public abstract class DecoratorTestCase {

  private static final String MODEL_PATH = "src/test/resources";

  public ASTCDCompilationUnit parse(String... names) {
    String qualifiedName = String.join(".", names);
    CD4AnalysisLanguage cd4AnalysisLanguage = new CD4AnalysisLanguage();
    CD4AnalysisModelLoader modelLoader = new CD4AnalysisModelLoader(cd4AnalysisLanguage);
    ModelPath modelPath = new ModelPath(Paths.get(MODEL_PATH));
    CD4AnalysisGlobalScope globalScope = new CD4AnalysisGlobalScope(modelPath, cd4AnalysisLanguage);
    Collection<ASTCDCompilationUnit> ast = modelLoader.loadModelsIntoScope(qualifiedName, modelPath, globalScope);
    if (ast.isEmpty())
      fail(String.format("Failed to load model '%s'", qualifiedName));
    if (ast.size() > 1)
      fail(String.format("Multiple models loaded model '%s'", qualifiedName));
    return ast.iterator().next();
  }
}
