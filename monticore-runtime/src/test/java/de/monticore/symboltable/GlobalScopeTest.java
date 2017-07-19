/*
 * ******************************************************************************
 * MontiCore Language Workbench
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

package de.monticore.symboltable;

import de.monticore.ModelingLanguage;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.mocks.languages.entity.EntityLanguage;
import de.monticore.symboltable.mocks.languages.entity.EntitySymbol;
import de.monticore.symboltable.resolving.CommonResolvingFilter;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 *
 * @author Pedram Mir Seyed Nazari
 */
public class GlobalScopeTest {
  
  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }

//  @Test
  public void testLoadTopLevelSymbol() {
    ModelingLanguage entityLanguage = new EntityLanguage();
    
    ModelPath modelPath = new ModelPath(Paths.get("src/test/resources/modelloader/modelpath"));

    ResolvingConfiguration resolverConfig = new ResolvingConfiguration();
    resolverConfig.addTopScopeResolver(CommonResolvingFilter.create(EntitySymbol.KIND));

    final MutableScope globalScope =
        new GlobalScope(modelPath, entityLanguage, resolverConfig);
    
    EntitySymbol entitySymbol = globalScope.<EntitySymbol> resolve("models.E", EntitySymbol.KIND).orElse(null);
    
    assertNotNull(entitySymbol);
    assertEquals("E", entitySymbol.getName());
    assertEquals("models.E", entitySymbol.getFullName());
    assertEquals("models", entitySymbol.getPackageName());
  }
}
