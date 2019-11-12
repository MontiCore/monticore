/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testsymtabmill;

import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.ImportStatement;
import de.monticore.testsymtabmill.testsymtabmill._symboltable.*;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MillTest {

  @Test
  public void testMill(){
    TestSymTabMillLanguage language = TestSymTabMillSymTabMill.testSymTabMillLanguageBuilder().build();
    TestSymTabMillModelLoader modelLoader = TestSymTabMillSymTabMill.testSymTabMillModelLoaderBuilder().setModelingLanguage(language).build();
    TestSymTabMillScope scope = TestSymTabMillSymTabMill.testSymTabMillScopeBuilder().build();
    TestSymTabMillArtifactScope artifactScope = TestSymTabMillSymTabMill.testSymTabMillArtifactScopeBuilder().addImport(new ImportStatement("a.b.c",false)).setPackageName("sym").build();
    TestSymTabMillGlobalScope globalScope = TestSymTabMillSymTabMill.testSymTabMillGlobalScopeBuilder().setTestSymTabMillLanguage(language).setModelPath(new ModelPath()).build();
    TestSymTabMillSymbolTableCreator symbolTableCreator = TestSymTabMillSymTabMill.testSymTabMillSymbolTableCreatorBuilder().addToScopeStack(scope).build();
    TestSymTabMillSymbolTableCreatorDelegator symbolTableCreatorDelegator = TestSymTabMillSymTabMill.testSymTabMillSymbolTableCreatorDelegatorBuilder().setGlobalScope(globalScope).build();

    assertTrue(language.getFileExtension().equals("ts"));
    assertTrue(modelLoader.getModelingLanguage().equals(language));
    assertFalse(scope.isShadowing());
    assertTrue(globalScope.getTestSymTabMillLanguage().equals(language));
    assertTrue(artifactScope.getImportList().get(0).getStatement().equals("a.b.c"));
    assertTrue(symbolTableCreator.getCurrentScope().get().equals(scope));
  }

}
