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

package mc.feature.interfaces;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import org.junit.Test;

import de.monticore.antlr4.MCConcreteParser.ParserExecution;
import mc.GeneratorIntegrationsTest;
import mc.feature.interfaces.sub._ast.ASTA;
import mc.feature.interfaces.sub._parser.SubParser;

public class InterfacesTest extends GeneratorIntegrationsTest {
  
  @Test
  public void test1a() throws IOException {
    
    SubParser parser = new SubParser();
    parser.setParserTarget(ParserExecution.EOF);
    
    Optional<mc.feature.interfaces.sub._ast.ASTA> ast = parser.parseA(new StringReader("Hello Otto Mustermann"));
    
    assertTrue(ast.get() instanceof ASTA);
    ASTA astA = ast.get();
    assertNotNull(astA.getB());
    assertTrue(astA.get_Children().size()==1);
  }
  
}
