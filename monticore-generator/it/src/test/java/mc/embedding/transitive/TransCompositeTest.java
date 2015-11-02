/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
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

package mc.embedding.transitive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;

import mc.GeneratorIntegrationsTest;
import mc.embedding.composite._symboltable.Text2ContentAdapter;
import mc.embedding.embedded._symboltable.TextSymbol;
import mc.embedding.host._symboltable.ContentSymbol;
import mc.embedding.host._symboltable.HostSymbol;
import mc.embedding.transitive.transcomposite._symboltable.TransCompositeLanguage;
import mc.embedding.transitive.transhost._symboltable.TransStartSymbol;

import org.junit.Test;

import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolverConfiguration;

public class TransCompositeTest extends GeneratorIntegrationsTest {

  @Test
  public void test() {
    final TransCompositeLanguage language = new TransCompositeLanguage();
    final ResolverConfiguration resolverConfiguration = new ResolverConfiguration();
    resolverConfiguration.addTopScopeResolvers(language.getResolvers());

    final ModelPath modelPath = new ModelPath(Paths.get("src/test/resources/mc/embedding/transitive/"));

    final GlobalScope scope = new GlobalScope(modelPath, language.getModelLoader(), resolverConfiguration);

    // Symbol of the host language
    final TransStartSymbol transHostSymbol = scope.<TransStartSymbol>resolve("TransComposite", TransStartSymbol.KIND).orElse(null);
    assertNotNull(transHostSymbol);
    assertEquals("TransComposite", transHostSymbol.getName());

    // Symbol of the embedded language
    final HostSymbol hostSymbol = transHostSymbol.getSpannedScope().<HostSymbol>resolve("TransHost", HostSymbol.KIND).orElse(null);
    assertNotNull(hostSymbol);

    // Symbol of the transitive embedded language
    final TextSymbol textSymbol = hostSymbol.getSpannedScope().<TextSymbol>resolve("Hello", TextSymbol.KIND).orElse(null);
    assertNotNull(textSymbol);

    // transitive adapted text symbol -> content symbol
    final ContentSymbol text2ContentSymbol = hostSymbol.getSpannedScope().<ContentSymbol>resolve("Hello", ContentSymbol.KIND).orElse(null);
    assertNotNull(text2ContentSymbol);
    assertTrue(text2ContentSymbol instanceof Text2ContentAdapter);

  }

}
