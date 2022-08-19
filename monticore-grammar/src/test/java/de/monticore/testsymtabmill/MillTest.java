/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testsymtabmill;

import de.monticore.io.paths.MCPath;
import de.monticore.testsymtabmill.testsymtabmill.TestSymTabMillMill;
import de.monticore.testsymtabmill.testsymtabmill._symboltable.*;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MillTest {
  
  @Before
  public void initLog() {
    LogStub.init();
    Log.enableFailQuick(false);
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
    assertTrue(symbolTableCreator.getCurrentScope().get().equals(scope));
  
    assertTrue(Log.getFindings().isEmpty());
  }

}
