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

import de.monticore.mchexnumbers._ast.ASTHexInteger;
import de.monticore.mchexnumbers._ast.ASTHexadecimal;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCHexNumbersPrettyPrinter;
import de.monticore.testmchexnumbers._parser.TestMCHexNumbersParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;

/**
 * @author npichler
 */

public class MCHexNumbersPrettyPrinterTest {
  
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
  public void testHexadecimal() throws IOException {
    TestMCHexNumbersParser parser = new TestMCHexNumbersParser();
    Optional<ASTHexadecimal> result = parser.parseHexadecimal(new StringReader("0X6b90A"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTHexadecimal hexadecimal = result.get();
    
    MCHexNumbersPrettyPrinter prettyPrinter = new MCHexNumbersPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(hexadecimal);
    
    result = parser.parseHexadecimal(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(hexadecimal.deepEquals(result.get()));
  }
  
  @Test
  public void testHexIntegerPositiv() throws IOException {
    TestMCHexNumbersParser parser = new TestMCHexNumbersParser();
    Optional<ASTHexInteger> result = parser.parseHexInteger(new StringReader("0X6b90A"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTHexInteger hexinteger = result.get();
    
    MCHexNumbersPrettyPrinter prettyPrinter = new MCHexNumbersPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(hexinteger);
    
    result = parser.parseHexInteger(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(hexinteger.deepEquals(result.get()));
  }
  
  @Test
  public void testHexIntegerNegative() throws IOException {
    TestMCHexNumbersParser parser = new TestMCHexNumbersParser();
    Optional<ASTHexInteger> result = parser.parseHexInteger(new StringReader("-0xaf67"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTHexInteger hexinteger = result.get();
    
    MCHexNumbersPrettyPrinter prettyPrinter = new MCHexNumbersPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(hexinteger);
    
    result = parser.parseHexInteger(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(hexinteger.deepEquals(result.get()));
  }
  
}
