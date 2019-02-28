package de.monticore.codegen.cd2java;

import de.monticore.io.paths.ModelPath;
import de.monticore.umlcd4a.CD4AnalysisLanguage;
import de.monticore.umlcd4a.CD4AnalysisModelLoader;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._parser.CD4AnalysisParser;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.fail;

public abstract class DecoratorTestCase {

  private static final String MODEL_PATH = "src/test/resources";

  private static final String CD_EXTENSION = ".cd";

  private static final CD4AnalysisParser parser = new CD4AnalysisParser();

  public ASTCDCompilationUnit parse(String... pathSegments) throws IOException {
    String path = Paths.get(MODEL_PATH, pathSegments).toAbsolutePath().toString() + CD_EXTENSION;
    Optional<ASTCDCompilationUnit> ast = parser.parse(path);
    if (!ast.isPresent())
      fail(String.format("Failed to load model '%s'", path));
    return ast.get();
  }

  public ASTCDCompilationUnit loadModel(String qualifiedName) {
    CD4AnalysisLanguage cd4AnalysisLanguage = new CD4AnalysisLanguage();
    CD4AnalysisModelLoader modelLoader = new CD4AnalysisModelLoader(cd4AnalysisLanguage);
    ModelPath modelPath = new ModelPath(Paths.get(MODEL_PATH));
    Optional<ASTCDCompilationUnit> ast = modelLoader.loadModel(qualifiedName, modelPath);
    if (!ast.isPresent())
      fail(String.format("Failed to load model '%s'", qualifiedName));
    return ast.get();
  }
}
