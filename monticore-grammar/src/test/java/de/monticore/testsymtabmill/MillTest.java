/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testsymtabmill;

import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.ImportStatement;
import de.monticore.testsymtabmill.testsymtabmill.TestSymTabMillMill;
import de.monticore.testsymtabmill.testsymtabmill._symboltable.*;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MillTest {

  @Test
  public void testMill(){
    TestSymTabMillScope scope = TestSymTabMillMill.testSymTabMillScopeBuilder().build();
    TestSymTabMillArtifactScope artifactScope = TestSymTabMillMill.testSymTabMillArtifactScopeBuilder().addImport(new ImportStatement("a.b.c",false)).setPackageName("sym").build();
    TestSymTabMillGlobalScope globalScope = TestSymTabMillMill.testSymTabMillGlobalScopeBuilder().setModelPath(new ModelPath()).build();
    TestSymTabMillSymbolTableCreator symbolTableCreator = TestSymTabMillMill.testSymTabMillSymbolTableCreatorBuilder().addToScopeStack(scope).build();
    TestSymTabMillSymbolTableCreatorDelegator symbolTableCreatorDelegator = TestSymTabMillMill.testSymTabMillSymbolTableCreatorDelegatorBuilder().setGlobalScope(globalScope).build();

    assertFalse(scope.isShadowing());
    assertTrue(artifactScope.getImportList().get(0).getStatement().equals("a.b.c"));
    assertTrue(symbolTableCreator.getCurrentScope().get().equals(scope));
  }

}
