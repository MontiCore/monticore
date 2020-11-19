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
    ITestSymTabMillScope scope = TestSymTabMillMill.testSymTabMillScope();
    ITestSymTabMillArtifactScope artifactScope = TestSymTabMillMill.testSymTabMillArtifactScope();
    artifactScope.setPackageName("sym");
    ITestSymTabMillGlobalScope globalScope = TestSymTabMillMill.testSymTabMillGlobalScope();
    globalScope.setModelPath(new ModelPath());
    globalScope.setModelFileExtension("mill");
    TestSymTabMillSymbolTableCreator symbolTableCreator = TestSymTabMillMill.testSymTabMillSymbolTableCreator();
    symbolTableCreator.putOnStack(scope);
    TestSymTabMillSymbolTableCreatorDelegator symbolTableCreatorDelegator = TestSymTabMillMill.testSymTabMillSymbolTableCreatorDelegator();

    assertFalse(scope.isShadowing());
    assertTrue(symbolTableCreator.getCurrentScope().get().equals(scope));
  }

}
