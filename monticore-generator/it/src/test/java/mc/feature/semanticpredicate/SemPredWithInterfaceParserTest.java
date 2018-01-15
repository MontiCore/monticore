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

package mc.feature.semanticpredicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.semanticpredicate.sempredwithinterface._ast.ASTISequence;
import mc.feature.semanticpredicate.sempredwithinterface._parser.SemPredWithInterfaceParser;

public class SemPredWithInterfaceParserTest extends GeneratorIntegrationsTest {
  
  @Test
  public void testParse() {
    String input = "foo foo";
    SemPredWithInterfaceParser p = new SemPredWithInterfaceParser();
    java.util.Optional<ASTISequence> ast = null;
    try {
       ast = p.parseISequence(new StringReader(input));
    } catch (IOException e) {
      fail();
    }
    assertTrue(ast.isPresent());
    ASTISequence seq = ast.get();
    assertEquals(2, seq.getIList().size());
    
    assertTrue(seq.getIList().get(0).isFirst());
    assertFalse(seq.getIList().get(1).isFirst());
  }
  
}
