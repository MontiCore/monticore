/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

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

/**
 * @author (last commit) $Author$
 */
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
