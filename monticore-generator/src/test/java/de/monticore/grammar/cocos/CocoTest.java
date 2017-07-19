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

package de.monticore.grammar.cocos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.monticore.GrammarGlobalScopeTestFactory;
import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;

/**
 * Created by
 *
 * @author KH
 */
public abstract class CocoTest {
  
  protected void testValidGrammar(String grammar, Grammar_WithConceptsCoCoChecker checker) {
    final Scope globalScope = GrammarGlobalScopeTestFactory.create();
    
    // test grammar symbol
    final MCGrammarSymbol grammarSymbol = (MCGrammarSymbol) globalScope
        .resolve(grammar,
            MCGrammarSymbol.KIND)
        .orElse(null);
    assertNotNull(grammarSymbol);
    assertTrue(grammarSymbol.getAstGrammar().isPresent());
    
    Log.getFindings().clear();
    checker.handle(grammarSymbol.getAstGrammar().get());
    
    assertTrue(Log.getFindings().isEmpty());
  }
  
  protected void testInvalidGrammar(String grammar, String code, String message,
      Grammar_WithConceptsCoCoChecker checker) {
    testInvalidGrammar(grammar, code, message, checker, 1);
  }
  
  protected void testInvalidGrammar(String grammar, String code, String message,
      Grammar_WithConceptsCoCoChecker checker, int numberOfFindings) {
    final Scope globalScope = GrammarGlobalScopeTestFactory.create();
    
    // test grammar symbol
    final MCGrammarSymbol grammarSymbol = (MCGrammarSymbol) globalScope
        .resolve(grammar,
            MCGrammarSymbol.KIND)
        .orElse(null);
    assertNotNull(grammarSymbol);
    assertTrue(grammarSymbol.getAstGrammar().isPresent());
    
    Log.getFindings().clear();
    checker.handle(grammarSymbol.getAstGrammar().get());
    
    assertFalse(Log.getFindings().isEmpty());
    assertEquals(numberOfFindings, Log.getFindings().size());
    for (Finding f : Log.getFindings()) {
      assertEquals(code + message, f.getMsg());
    }
  }
  
  protected void testInvalidGrammarKeepFindings(String grammar, String code, String message,
      Grammar_WithConceptsCoCoChecker checker) {
    final Scope globalScope = GrammarGlobalScopeTestFactory.create();
    
    // test grammar symbol
    final MCGrammarSymbol grammarSymbol = (MCGrammarSymbol) globalScope
        .resolve(grammar,
            MCGrammarSymbol.KIND)
        .orElse(null);
    assertNotNull(grammarSymbol);
    assertTrue(grammarSymbol.getAstGrammar().isPresent());
    checker.handle(grammarSymbol.getAstGrammar().get());
    assertFalse(Log.getFindings().isEmpty());
    assertEquals(1, Log.getFindings().size());
    for (Finding f : Log.getFindings()) {
      assertEquals(code + message, f.getMsg());
    }
  }
}
