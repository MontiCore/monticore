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

package de.monticore.prettyprint;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.UMLModifierPrettyPrinter;
import de.monticore.testumlmodifier._parser.TestUMLModifierParser;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;

/**
 * @author npichler
 */

public class UMLModifierPrettyPrinterTest {
  
  @BeforeClass
  public static void init() {
    // replace log by a sideffect free variant
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Before
  public void setUp() {
    Log.getFindings().clear();
  }
  
  @Test
  public void testModifierWord() throws IOException {
    TestUMLModifierParser parser = new TestUMLModifierParser();
    Optional<ASTModifier> result = parser.parseModifier(new StringReader("private"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTModifier modifier = result.get();
    
    UMLModifierPrettyPrinter prettyPrinter = new UMLModifierPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(modifier);
    
    result = parser.parseModifier(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(modifier.deepEquals(result.get()));
  }
  
  @Test
  public void testModifierSymbol() throws IOException {
    TestUMLModifierParser parser = new TestUMLModifierParser();
    Optional<ASTModifier> result = parser.parseModifier(new StringReader("-"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTModifier modifier = result.get();
    
    UMLModifierPrettyPrinter prettyPrinter = new UMLModifierPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(modifier);
    
    result = parser.parseModifier(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(modifier.deepEquals(result.get()));
  }
}
