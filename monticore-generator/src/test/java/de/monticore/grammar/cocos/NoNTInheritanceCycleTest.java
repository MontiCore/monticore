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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.monticore.GrammarGlobalScopeTestFactory;
import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.monticore.grammar.symboltable.EssentialMCGrammarSymbol;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;

/**
 * Created by
 *
 * @author KH
 */
public class NoNTInheritanceCycleTest extends CocoTest{

  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A4022.A4022";

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
    checker.addCoCo( new NoNTInheritanceCycle());
  }

  // @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, NoNTInheritanceCycle.ERROR_CODE,
        String.format(NoNTInheritanceCycle.ERROR_MSG_FORMAT, "cocos.invalid.A4022.A4022.A"), checker);
  }

  //  @Test
  public void testInvalid2() {

    final Scope globalScope = GrammarGlobalScopeTestFactory.create();

    // test grammar symbol
    final EssentialMCGrammarSymbol grammarSymbol = (EssentialMCGrammarSymbol) globalScope.resolve(grammar+ "b",
        EssentialMCGrammarSymbol.KIND).orElse(null);
    assertNotNull(grammarSymbol);
    assertTrue(grammarSymbol.getAstGrammar().isPresent());

    Log.getFindings().clear();
    checker.handle(grammarSymbol.getAstGrammar().get());

    assertEquals(2, Log.getFindings().size());
    assertEquals(NoNTInheritanceCycle.ERROR_CODE + String.format(NoNTInheritanceCycle.ERROR_MSG_FORMAT, "cocos.invalid.A4022.A4022b.A"),
                 Log.getFindings().get(0).getMsg());
    assertEquals(NoNTInheritanceCycle.ERROR_CODE + String.format(NoNTInheritanceCycle.ERROR_MSG_FORMAT, "cocos.invalid.A4022.A4022b.B"),
                 Log.getFindings().get(1).getMsg());

  }

  @Test @Ignore
  public void testValid(){
    testValidGrammar("cocos.valid.ExtendNTs", checker);
  }

}
