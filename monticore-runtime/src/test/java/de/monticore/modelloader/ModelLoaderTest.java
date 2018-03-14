/* (c) https://github.com/MontiCore/monticore */

package de.monticore.modelloader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.nio.file.Paths;

import de.monticore.AmbiguityException;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.mocks.languages.entity.EntityLanguage;
import de.monticore.symboltable.mocks.languages.entity.EntityLanguageModelLoader;
import de.monticore.symboltable.mocks.languages.entity.asts.ASTEntity;
import de.monticore.symboltable.mocks.languages.entity.asts.ASTEntityCompilationUnit;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class ModelLoaderTest {
  
  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }
  
  @Test
  @Ignore
  public void testLoadModelWithUniqueName() {
    final EntityLanguageModelLoader modelLoader = new EntityLanguage().getModelLoader();
    
    final ModelPath modelPath = new ModelPath(Paths.get("src/test/resources/modelloader/modelpath"));
    
    ASTEntityCompilationUnit loadedAst = null;
    try {
      loadedAst = modelLoader.loadModel("models.E", modelPath).orElse(null);
    }
    catch (AmbiguityException e) {
      fail();
    }
    
    assertNotNull(loadedAst);
    ASTEntity classNode = loadedAst.getClassNode();
    
    assertEquals("E", classNode.getName());
  }
  
  @Test
  @Ignore
  public void testLoadModelWithAmbiguousNameButUniqueModelingLanguage() {
    final EntityLanguageModelLoader modelLoader = new EntityLanguage().getModelLoader();
    
    final ModelPath modelPath = new ModelPath(Paths.get("src/test/resources/modelloader/modelpath"));
    
    ASTEntityCompilationUnit loadedAst = null;
    try {
      loadedAst = modelLoader.loadModel("models.B", modelPath).orElse(null);
    }
    catch (AmbiguityException e) {
      fail();
    }
    
    assertNotNull(loadedAst);
    assertEquals("B", loadedAst.getClassNode().getName());
  }
  
  @Test
  public void testLoadAmbiguousModels() {
    final EntityLanguageModelLoader entityModelLoader = new EntityLanguage().getModelLoader();
    
    final ModelPath modelPath = new ModelPath(
        Paths.get("src/test/resources/modelloader/modelpath"),
        Paths.get("src/test/resources/modelloader/modelpath2"));
    
    entityModelLoader.loadModels("models.D", modelPath);
    
    assertEquals(2, Log.getErrorCount());
  }
  
  @Test
  @Ignore
  public void testLoadModelFromJar() {
    final EntityLanguageModelLoader modelLoader = new EntityLanguage().getModelLoader();
    
    final ModelPath modelPath = new ModelPath(
        Paths.get("src/test/resources/modelloader/modeljar.jar"));
    
    ASTEntityCompilationUnit loadedAst = null;
    try {
      loadedAst = modelLoader.loadModel("models.C", modelPath).orElse(null);
    }
    catch (AmbiguityException e) {
      fail();
    }
    
    assertNotNull(loadedAst);
    assertEquals("C", loadedAst.getClassNode().getName());
  }
  
}
