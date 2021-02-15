/* (c) https://github.com/MontiCore/monticore */
package mc.feature.filefindertest;

import de.monticore.io.FileReaderWriter;
import de.monticore.io.paths.ModelPath;
import mc.feature.filefindertest.filefindertest.FileFinderTestMill;
import mc.feature.filefindertest.filefindertest._ast.ASTSCArtifact;
import mc.feature.filefindertest.filefindertest._parser.FileFinderTestParser;
import mc.feature.filefindertest.filefindertest._symboltable.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FileFinderTest {

  @Before
  public void setUp() throws IOException {
    FileFinderTestParser parser = FileFinderTestMill.parser();
    Optional<ASTSCArtifact> artifact = parser.parse("src/test/resources/mc/feature/filefindertest/Model1.sc");
    Optional<ASTSCArtifact> artifactII = parser.parse("src/test/resources/mc/feature/filefindertest/Model2.sc");
    FileFinderTestScopesGenitorDelegator delegator = FileFinderTestMill.scopesGenitorDelegator();
    FileFinderTestScopesGenitorDelegator delegatorII = FileFinderTestMill.scopesGenitorDelegator();
    assertTrue(artifact.isPresent());
    assertTrue(artifactII.isPresent());
    IFileFinderTestArtifactScope scope = delegator.createFromAST(artifact.get());
    scope.setPackageName("mc.feature.filefindertest");
    IFileFinderTestArtifactScope scopeII = delegatorII.createFromAST(artifactII.get());
    scopeII.setPackageName("mc.feature.filefindertest");
    FileFinderTestDeSer deSer = new FileFinderTestDeSer();
    String serialized = deSer.serialize(scope);
    String serializedII = deSer.serialize(scopeII);
    FileReaderWriter.storeInFile(Paths.get("src/test/resources/mc/feature/filefindertest/Model2.scsym"), serializedII);
    FileReaderWriter.storeInFile(Paths.get("src/test/resources/mc/feature/filefindertest/Model1.scsym"), serialized);
    FileReaderWriter.storeInFile(Paths.get("src/test/resources/mc/feature/filefindertest/Model1.json"), serialized);

  }

  @Test
  public void fileFinderDetectsCorrectModelRegex() {
    IFileFinderTestGlobalScope gs = FileFinderTestMill.globalScope();
    gs.clear();
    gs.setModelPath(new ModelPath(Paths.get("src/test/resources")));
    Optional<StatechartSymbol> statechartSymbol = gs.resolveStatechart("mc.feature.filefindertest.Model1");
    assertTrue(statechartSymbol.isPresent());
  }

  @Test
  public void fileFinderDetectsCorrectModel() {
    IFileFinderTestGlobalScope gs = FileFinderTestMill.globalScope();
    gs.clear();
    gs.setFileExt("scsym");
    gs.setModelPath(new ModelPath(Paths.get("src/test/resources")));
    Optional<StatechartSymbol> statechartSymbol = gs.resolveStatechart("mc.feature.filefindertest.Model1");
    assertTrue(statechartSymbol.isPresent());
  }

  @Test
  public void fileFinderNotDetectsCorrectModelWorkingPath() {
    IFileFinderTestGlobalScope gs = FileFinderTestMill.globalScope();
    gs.clear();
    gs.setFileExt("ym");
    gs.setModelPath(new ModelPath(Paths.get("src/test/resources")));
    Optional<StatechartSymbol> statechartSymbol = gs.resolveStatechart("mc.feature.filefindertest.Model1");
    assertFalse(statechartSymbol.isPresent());
  }

  @Test
  public void fileFinderNotDetectsCorrectModelBadPath() {
    IFileFinderTestGlobalScope gs = FileFinderTestMill.globalScope();
    gs.clear();
    gs.setModelPath(new ModelPath(Paths.get("src/test")));
    Optional<StatechartSymbol> statechartSymbol = gs.resolveStatechart("mc.feature.filefindertest.Model1");
    assertFalse(statechartSymbol.isPresent());
  }

  @Test
  public void fileFinderNotDetectsCorrectModelEmptyPath() {
    IFileFinderTestGlobalScope gs = FileFinderTestMill.globalScope();
    gs.clear();
    gs.setModelPath(new ModelPath());
    Optional<StatechartSymbol> statechartSymbol = gs.resolveStatechart("mc.feature.filefindertest.Model1");
    assertFalse(statechartSymbol.isPresent());
  }

  @Test
  public void fileFinderDetectsCorrectModelCustomFileExt() {
    IFileFinderTestGlobalScope gs = FileFinderTestMill.globalScope();
    gs.clear();
    gs.setFileExt("json");
    gs.setModelPath(new ModelPath(Paths.get("src/test/resources")));
    Optional<StatechartSymbol> statechartSymbol = gs.resolveStatechart("mc.feature.filefindertest.Model1");
    assertTrue(statechartSymbol.isPresent());
  }
}
