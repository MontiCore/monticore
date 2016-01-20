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

package mc.feature.automaton;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.automaton.automaton._parser.AutomatonParser;
import mc.feature.automaton.automaton._ast.ASTSubTransition;
import mc.feature.automaton.automaton._ast.ASTTransition;

public class SubclassParsingTest extends GeneratorIntegrationsTest {
  
  @Test
  public void testSubtypeParsing() throws IOException {
    
    AutomatonParser parser = new AutomatonParser();
    
    Optional<ASTTransition> ast = parser.parseTransition(new StringReader("sub a -x> b;"));
    assertTrue(ast.isPresent());
    assertTrue(ast.get() instanceof ASTSubTransition);
    
  }
}
