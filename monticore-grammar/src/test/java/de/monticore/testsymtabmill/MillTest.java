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
    TestSymTabMillLanguage language = TestSymTabMillMill.testSymTabMillLanguageBuilder().build();
    TestSymTabMillModelLoader modelLoader = TestSymTabMillMill.testSymTabMillModelLoaderBuilder().setModelingLanguage(language).build();
    TestSymTabMillScope scope = TestSymTabMillMill.testSymTabMillScopeBuilder().build();
    TestSymTabMillArtifactScope artifactScope = TestSymTabMillMill.testSymTabMillArtifactScopeBuilder().addImport(new ImportStatement("a.b.c",false)).setPackageName("sym").build();
    TestSymTabMillGlobalScope globalScope = TestSymTabMillMill.testSymTabMillGlobalScopeBuilder().setTestSymTabMillLanguage(language).setModelPath(new ModelPath()).build();
    TestSymTabMillSymbolTableCreator symbolTableCreator = TestSymTabMillMill.testSymTabMillSymbolTableCreatorBuilder().addToScopeStack(scope).build();
    TestSymTabMillSymbolTableCreatorDelegator symbolTableCreatorDelegator = TestSymTabMillMill.testSymTabMillSymbolTableCreatorDelegatorBuilder().setGlobalScope(globalScope).build();

    assertTrue(language.getFileExtension().equals("ts"));
    assertTrue(modelLoader.getModelingLanguage().equals(language));
    assertFalse(scope.isShadowing());
    assertTrue(globalScope.getTestSymTabMillLanguage().equals(language));
    assertTrue(artifactScope.getImportList().get(0).getStatement().equals("a.b.c"));
    assertTrue(symbolTableCreator.getCurrentScope().get().equals(scope));
  }

}
