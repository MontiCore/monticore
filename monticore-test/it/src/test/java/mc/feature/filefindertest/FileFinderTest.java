/* (c) https://github.com/MontiCore/monticore */
package mc.feature.filefindertest;

import de.monticore.io.FileReaderWriter;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.feature.filefindertest.filefindertest.FileFinderTestMill;
import mc.feature.filefindertest.filefindertest._ast.ASTSCArtifact;
import mc.feature.filefindertest.filefindertest._parser.FileFinderTestParser;
import mc.feature.filefindertest.filefindertest._symboltable.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FileFinderTest {
  
  protected static final String SYMBOL_PATH = "target/test/resources";
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
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
    FileFinderTestSymbols2Json symbols2Json = new FileFinderTestSymbols2Json();
    String serialized = symbols2Json.serialize(scope);
    String serializedII = symbols2Json.serialize(scopeII);
    FileReaderWriter.storeInFile(Paths.get(SYMBOL_PATH + "/mc/feature/filefindertest/Model2.scsym"), serializedII);
    FileReaderWriter.storeInFile(Paths.get(SYMBOL_PATH + "/mc/feature/filefindertest/Model1.scsym"), serialized);
    FileReaderWriter.storeInFile(Paths.get(SYMBOL_PATH + "/mc/feature/filefindertest/Model1.json"), serialized);
  }

  @Test
  public void testFileFinder1() {
    //fileFinder detects the correct Modles with the standard Regex.
    IFileFinderTestGlobalScope gs = FileFinderTestMill.globalScope();
    gs.clear();
    gs.setSymbolPath(new MCPath(Paths.get(SYMBOL_PATH)));
    Optional<StatechartSymbol> statechartSymbol = gs.resolveStatechart("mc.feature.filefindertest.Model1");
    assertTrue(statechartSymbol.isPresent());
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testFileFinder2() {
    // fileFinder detects the correct Model with the correct file Extension
    IFileFinderTestGlobalScope gs = FileFinderTestMill.globalScope();
    gs.clear();
    gs.setFileExt("scsym");
    gs.setSymbolPath(new MCPath(Paths.get(SYMBOL_PATH)));
    Optional<StatechartSymbol> statechartSymbol = gs.resolveStatechart("mc.feature.filefindertest.Model1");
    assertTrue(statechartSymbol.isPresent());
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testFileFinder3() {
    //fileFinder does not detect any Models, Wrong file Extension.
    IFileFinderTestGlobalScope gs = FileFinderTestMill.globalScope();
    gs.clear();
    gs.setFileExt("ym");
    gs.setSymbolPath(new MCPath(Paths.get(SYMBOL_PATH)));
    Optional<StatechartSymbol> statechartSymbol = gs.resolveStatechart("mc.feature.filefindertest.Model1");
    assertFalse(statechartSymbol.isPresent());
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testFileFinder4() {
    //fileFinder finds no Models, wrong Model Path.
    IFileFinderTestGlobalScope gs = FileFinderTestMill.globalScope();
    gs.clear();
    gs.setSymbolPath(new MCPath(Paths.get("src/test")));
    Optional<StatechartSymbol> statechartSymbol = gs.resolveStatechart("mc.feature.filefindertest.Model1");
    assertFalse(statechartSymbol.isPresent());
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testFileFinder5() {
    //fileFinder finds no Models, empty Model Path.
    IFileFinderTestGlobalScope gs = FileFinderTestMill.globalScope();
    gs.clear();
    gs.setSymbolPath(new MCPath());
    Optional<StatechartSymbol> statechartSymbol = gs.resolveStatechart("mc.feature.filefindertest.Model1");
    assertFalse(statechartSymbol.isPresent());
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testFileFinder6() {
    //fileFinder detects 1 Model with custom File Extension
    IFileFinderTestGlobalScope gs = FileFinderTestMill.globalScope();
    gs.clear();
    gs.setFileExt("json");
    gs.setSymbolPath(new MCPath(Paths.get(SYMBOL_PATH)));
    Optional<StatechartSymbol> statechartSymbol = gs.resolveStatechart("mc.feature.filefindertest.Model1");
    assertTrue(statechartSymbol.isPresent());
    assertTrue(Log.getFindings().isEmpty());
  }
}
