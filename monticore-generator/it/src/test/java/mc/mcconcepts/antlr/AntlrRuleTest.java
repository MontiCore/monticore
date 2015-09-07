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

package mc.mcconcepts.antlr;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import mc.GeneratorIntegrationsTest;

import org.junit.Test;

import de.monticore.grammar.grammar_withconcepts._parser.BreakStatementMCParser;
import de.monticore.grammar.grammar_withconcepts._parser.Grammar_WithConceptsParserFactory;

public class AntlrRuleTest extends GeneratorIntegrationsTest {
  
  @Test
  public void testCorrect() throws IOException {
    
    StringReader s = new StringReader("break test_new;");
    
    BreakStatementMCParser parser = Grammar_WithConceptsParserFactory.createBreakStatementMCParser();
    parser.parse(s);
    assertEquals(false, parser.hasErrors());
       
  }
  
  @Test
  public void testFalse() throws IOException {
    
    StringReader s = new StringReader("- test");
    
    BreakStatementMCParser parser = Grammar_WithConceptsParserFactory.createBreakStatementMCParser();
    parser.parse(s);
    assertEquals(true, parser.hasErrors());
       
  }

}
