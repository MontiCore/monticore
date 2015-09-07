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

package mc.examples.automaton;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Optional;

import mc.GeneratorIntegrationsTest;
import mc.examples.automaton.automaton._ast.ASTAutomaton;
import mc.examples.automaton.automaton._parser.AutomatonMCParser;
import mc.examples.automaton.automaton._parser.AutomatonParserFactory;

import org.junit.Test;

public class TestAutomaton extends GeneratorIntegrationsTest {
  
  @Test
  public void testParser() throws IOException {
    AutomatonMCParser parser = AutomatonParserFactory.createAutomatonMCParser();
    Optional<ASTAutomaton> optAutomaton;
    optAutomaton = parser.parse("src/test/resources/examples/automaton/Testautomat.aut");
    assertFalse(parser.hasErrors());
    assertTrue(optAutomaton.isPresent());
  }
  
}
