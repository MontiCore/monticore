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
package de.monticore.codegen.emf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;
import org.eclipse.emf.compare.diff.metamodel.DiffElement;
import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.emf.util.compare.AstEmfDiffUtility;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar_withconcepts._parser.Grammar_WithConceptsParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.Slf4jLog;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 */
public class GrammarDiffsTest {
  
  @BeforeClass
  public static void setup() {
    Slf4jLog.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testAstGrammarDiffs() {
    try {
      Optional<ASTMCGrammar> grammar1 = new Grammar_WithConceptsParser()
          .parse("src/test/resources/de/monticore/emf/Automaton.mc4");
          
      Optional<ASTMCGrammar> grammar2 = new Grammar_WithConceptsParser()
          .parse("src/test/resources/de/monticore/emf/Automaton2.mc4");
      
      if (grammar1.isPresent() && grammar2.isPresent()) {
      
        List<DiffElement> diffs = AstEmfDiffUtility.getAllAstDiffs(grammar2.get(), grammar1.get());
        
        AstEmfDiffUtility.printAstDiffsHierarchical(grammar2.get(), grammar1.get());
        
        assertEquals(5, diffs.size());
        
        assertEquals("Attribute Name in Automaton2 has changed from Automaton to Automaton2",
            diffs.get(0).toString());
        
        assertEquals("Action has been added", diffs.get(1).toString());
        
        assertTrue(diffs.get(2).toString().contains("ASTTerminal"));
        assertTrue(diffs.get(2).toString().contains(">>"));
        assertTrue(diffs.get(2).toString().contains("has been added"));
        
        assertTrue(diffs.get(3).toString().contains("ASTNonTerminal"));
        assertTrue(diffs.get(3).toString().contains("Action"));
        assertTrue(diffs.get(3).toString().contains("has been added"));
        
        assertTrue(diffs.get(4).toString().contains("ASTTerminal"));
        assertTrue(diffs.get(4).toString().contains(">"));
        assertTrue(diffs.get(4).toString().contains("has been removed"));
        
      }
      else {
        fail("Parse errors");
      }
    }
    catch (RecognitionException | IOException e) {
      fail("Should not reach this, but: " + e);
    }
    catch (InterruptedException e) {
      fail("Should not reach this, but: " + e);
    }
  }
  
}
