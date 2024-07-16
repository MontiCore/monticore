/* (c) https://github.com/MontiCore/monticore */
package mc.feature.multipletopsymbols;

import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.feature.multipletopsymbols.statechart.StatechartMill;
import mc.feature.multipletopsymbols.statechart._ast.ASTSCArtifact;
import mc.feature.multipletopsymbols.statechart._parser.StatechartParser;
import mc.feature.multipletopsymbols.statechart._symboltable.IStatechartArtifactScope;
import mc.feature.multipletopsymbols.statechart._symboltable.IStatechartGlobalScope;
import mc.feature.multipletopsymbols.statechart._symboltable.StateSymbol;
import mc.feature.multipletopsymbols.statechart._symboltable.StatechartSymbol;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StatechartResolvingTest {
  
  protected static IStatechartGlobalScope gs;

  @BeforeAll
  public static void setup() {
    gs = StatechartMill.globalScope();
    gs.setSymbolPath(new MCPath(Paths.get("src/test/resources")));
    gs.setFileExt("sc");
  }
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testResolving() throws IOException {
    StatechartParser parser = StatechartMill.parser();
    Optional<ASTSCArtifact> artifact = parser.parse("src/test/resources/mc/feature/multipletopsymbols/MyStatechart.sc");
    Assertions.assertTrue(artifact.isPresent());
    Assertions.assertFalse(parser.hasErrors());

    IStatechartArtifactScope as = StatechartMill.scopesGenitorDelegator().createFromAST(artifact.get());
    String packageName = String.join(".", artifact.get().getPackageDeclaration().getQualifiedName().getPartList());
    as.setPackageName(packageName);
    as.setName("MyStatechart");

    gs.addSubScope(as);

    Optional<StatechartSymbol> myStatechart = gs.resolveStatechart("mc.feature.multipletopsymbols.MyStatechart");
    Optional<StatechartSymbol> mySC = gs.resolveStatechart("mc.feature.multipletopsymbols.MyStatechart.MySC");
    Optional<StateSymbol> s = gs.resolveState("mc.feature.multipletopsymbols.MyStatechart.s");
    Optional<StateSymbol> t = gs.resolveState("mc.feature.multipletopsymbols.MyStatechart.s.t");
    Optional<StateSymbol> s2 = gs.resolveState("mc.feature.multipletopsymbols.MyStatechart.MySC.s");
    Optional<StateSymbol> u = gs.resolveState("mc.feature.multipletopsymbols.MyStatechart.MySC.u");
    Assertions.assertTrue(myStatechart.isPresent());
    Assertions.assertTrue(mySC.isPresent());
    Assertions.assertTrue(s.isPresent());
    Assertions.assertTrue(t.isPresent());
    Assertions.assertTrue(s2.isPresent());
    Assertions.assertTrue(u.isPresent());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


}
