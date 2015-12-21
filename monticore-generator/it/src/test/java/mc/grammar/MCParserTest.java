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

import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.grammar.grammar._ast.ASTMCGrammar;
import mc.grammar.grammar_withconcepts._parser.Grammar_WithConceptsParser;

public class MCParserTest extends GeneratorIntegrationsTest {
  
  @Test
  public void test1() throws IOException {
    
    Grammar_WithConceptsParser parser = new Grammar_WithConceptsParser();
    
    Optional<ASTMCGrammar> ast = parser.parseMCGrammar("src/test/resources/mc/grammar/SimpleGrammarWithConcept.mc4");
    
    assertTrue(ast.isPresent());
    
    
  }
}
