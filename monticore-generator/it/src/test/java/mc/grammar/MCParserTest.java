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

package mc.grammar;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Optional;

import mc.GeneratorIntegrationsTest;
import mc.grammar.grammar._ast.ASTMCGrammar;
import mc.grammar.grammar_withconcepts._parser.Grammar_WithConceptsParserFactory;
import mc.grammar.grammar_withconcepts._parser.MCGrammarMCParser;

import org.junit.Test;

public class MCParserTest extends GeneratorIntegrationsTest {
  
  @Test
  public void test1() throws IOException {
    
    MCGrammarMCParser parser = Grammar_WithConceptsParserFactory.createMCGrammarMCParser();
    
    Optional<ASTMCGrammar> ast = parser.parse("src/test/resources/mc/grammar/SimpleGrammarWithConcept.mc4");
    
    assertTrue(ast.isPresent());
    
    
  }
}
