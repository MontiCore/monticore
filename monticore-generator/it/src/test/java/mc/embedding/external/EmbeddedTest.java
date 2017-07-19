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

package mc.embedding.external;

import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolvingConfiguration;
import mc.GeneratorIntegrationsTest;
import mc.embedding.embedded._symboltable.EmbeddedLanguage;
import mc.embedding.embedded._symboltable.TextSymbol;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EmbeddedTest extends GeneratorIntegrationsTest {

  @Test
  public void test() {
    final EmbeddedLanguage language = new EmbeddedLanguage();
    final ResolvingConfiguration resolvingConfiguration = new ResolvingConfiguration();
    resolvingConfiguration.addTopScopeResolvers(language.getResolvingFilters());

    final ModelPath modelPath = new ModelPath(Paths.get("src/test/resources/mc/embedding"));

    final GlobalScope scope = new GlobalScope(modelPath, language, resolvingConfiguration);

    final TextSymbol textSymbol = scope.<TextSymbol>resolve("E", TextSymbol.KIND).orElse(null);
    assertNotNull(textSymbol);
    assertEquals("E", textSymbol.getName());
  }

}
