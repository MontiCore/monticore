/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testsymtabmill;

import de.monticore.io.paths.MCPath;
import de.monticore.testsymtabmill.testsymtabmill.TestSymTabMillMill;
import de.monticore.testsymtabmill.testsymtabmill._symboltable.*;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MillTest {
  
  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestSymTabMillMill.reset();
    TestSymTabMillMill.init();
  }
  
  @Test
  public void testMill(){
    ITestSymTabMillScope scope = TestSymTabMillMill.scope();
    ITestSymTabMillArtifactScope artifactScope = TestSymTabMillMill.artifactScope();
    artifactScope.setPackageName("sym");
    ITestSymTabMillGlobalScope globalScope = TestSymTabMillMill.globalScope();
    globalScope.setSymbolPath(new MCPath());
    globalScope.setFileExt("mill");
    TestSymTabMillScopesGenitor symbolTableCreator = TestSymTabMillMill.scopesGenitor();
    symbolTableCreator.putOnStack(scope);

    TestSymTabMillScopesGenitorDelegator symbolTableCreatorDelegator = TestSymTabMillMill.scopesGenitorDelegator();

    assertFalse(scope.isShadowing());
    assertEquals(symbolTableCreator.getCurrentScope().get(), scope);
  
    assertTrue(Log.getFindings().isEmpty());
  }

}
