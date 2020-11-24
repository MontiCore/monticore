/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testsymtabmill;

import de.monticore.io.paths.ModelPath;
import de.monticore.testsymtabmill.testsymtabmill.TestSymTabMillMill;
import de.monticore.testsymtabmill.testsymtabmill._symboltable.*;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MillTest {

  @Test
  public void testMill(){
    ITestSymTabMillScope scope = TestSymTabMillMill.scope();
    ITestSymTabMillArtifactScope artifactScope = TestSymTabMillMill.artifactScope();
    artifactScope.setPackageName("sym");
    ITestSymTabMillGlobalScope globalScope = TestSymTabMillMill.globalScope();
    globalScope.setModelPath(new ModelPath());
    globalScope.setFileExt("mill");
    TestSymTabMillSymbolTableCreator symbolTableCreator = TestSymTabMillMill.testSymTabMillSymbolTableCreator();
    symbolTableCreator.putOnStack(scope);
    TestSymTabMillSymbolTableCreatorDelegator symbolTableCreatorDelegator = TestSymTabMillMill.testSymTabMillSymbolTableCreatorDelegator();

    assertFalse(scope.isShadowing());
    assertTrue(symbolTableCreator.getCurrentScope().get().equals(scope));
  }

}
