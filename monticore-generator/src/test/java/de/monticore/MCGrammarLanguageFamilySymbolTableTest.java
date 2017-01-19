/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2016, MontiCore, All rights reserved.
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

package de.monticore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;
import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.se_rwth.commons.logging.Log;

/**
 * @author  Pedram Mir Seyed Nazari
 */
public class MCGrammarLanguageFamilySymbolTableTest {

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }

  @Test
  public void testSymbolTableOfGrammarStatechartDSL() {
    final ModelingLanguageFamily family = new MontiCoreGrammarLanguageFamily();
    final GlobalScope globalScope = new GlobalScope(new ModelPath(Paths.get("src/test/resources")), family);

    final Optional<MCGrammarSymbol> oldGrammar =
        globalScope.resolve("de.monticore.statechart.Statechart", MCGrammarSymbol.KIND);
    assertTrue(oldGrammar.isPresent());


    final Optional<MCGrammarSymbol> newGrammar =
        globalScope.resolve("de.monticore.statechart.Statechart", MCGrammarSymbol.KIND);
    assertTrue(newGrammar.isPresent());

    // 2 = Statechart grammar symbol and TestLexicals grammar symbol (super grammar of Statechart)
    assertEquals(2, globalScope.getSubScopes().size());
  }


}
