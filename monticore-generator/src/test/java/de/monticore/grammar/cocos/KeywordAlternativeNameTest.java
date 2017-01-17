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

package de.monticore.grammar.cocos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.GrammarGlobalScopeTestFactory;
import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.monticore.languages.grammar.MCGrammarSymbol;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;

/**
 * Created by
 *
 * @author KH
 */
public class KeywordAlternativeNameTest extends CocoTest {
  private final String MESSAGE = " The production A must not use an alternative of keywords without naming it.";
  private final String grammar = "cocos.invalid.A4019.A4019";

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }

  @Test
  public void testKeywordAlternativeWithoutName() throws IllegalArgumentException {
    final Scope globalScope = GrammarGlobalScopeTestFactory.create();

    Log.getFindings().clear();

    // test grammar symbol
    try {
      globalScope.resolve(grammar,  MCGrammarSymbol.KIND).orElse(null);
      fail("IllegalArgumentException expected");
    } catch (IllegalArgumentException e) {

      assertFalse(Log.getFindings().isEmpty());
      assertEquals(1, Log.getFindings().size());
      for (Finding f : Log.getFindings()) {
        assertEquals(KeywordAlternativeName.ERROR_CODE + MESSAGE, f.getMsg());
      }
    }
  }

  @Test
  public void testSingleKeyword(){
    Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new KeywordAlternativeName());
    testValidGrammar("cocos.valid.Attributes", checker);
  }


}
