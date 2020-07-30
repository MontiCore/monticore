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
    ITestSymTabMillScope scope = TestSymTabMillMill.testSymTabMillScopeBuilder().build();
    ITestSymTabMillArtifactScope artifactScope = TestSymTabMillMill.testSymTabMillArtifactScopeBuilder().setPackageName("sym").build();
    ITestSymTabMillGlobalScope globalScope = TestSymTabMillMill.testSymTabMillGlobalScopeBuilder().setModelPath(new ModelPath()).setModelFileExtension("mill").build();
    TestSymTabMillSymbolTableCreator symbolTableCreator = TestSymTabMillMill.testSymTabMillSymbolTableCreatorBuilder().addToScopeStack(scope).build();
    TestSymTabMillSymbolTableCreatorDelegator symbolTableCreatorDelegator = TestSymTabMillMill.testSymTabMillSymbolTableCreatorDelegatorBuilder().setGlobalScope(globalScope).build();

    assertFalse(scope.isShadowing());
    assertTrue(symbolTableCreator.getCurrentScope().get().equals(scope));
  }

}
