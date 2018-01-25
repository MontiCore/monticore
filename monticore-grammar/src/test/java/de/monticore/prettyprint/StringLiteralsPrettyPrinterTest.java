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
import de.monticore.prettyprint.StringLiteralsPrettyPrinter;
import de.monticore.stringliterals._ast.ASTCharLiteral;
import de.monticore.stringliterals._ast.ASTStringLiteral;
import de.monticore.teststringliterals._parser.TestStringLiteralsParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;

/**
 * @author npichler
 */

public class StringLiteralsPrettyPrinterTest {
  
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
  public void testCharLiteralEscapeSequenz() throws IOException {
    TestStringLiteralsParser parser = new TestStringLiteralsParser();
    Optional<ASTCharLiteral> result = parser.parseCharLiteral(new StringReader("'\"'"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTCharLiteral cliteral = result.get();
    
    StringLiteralsPrettyPrinter prettyPrinter = new StringLiteralsPrettyPrinter(
        new IndentPrinter());
    String output = prettyPrinter.prettyprint(cliteral);
    
    result = parser.parseCharLiteral(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(cliteral.deepEquals(result.get()));
  }
  
  @Test
  public void testCharLiteral() throws IOException {
    TestStringLiteralsParser parser = new TestStringLiteralsParser();
    Optional<ASTCharLiteral> result = parser.parseCharLiteral(new StringReader("'c'"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTCharLiteral cliteral = result.get();
    
    StringLiteralsPrettyPrinter prettyPrinter = new StringLiteralsPrettyPrinter(
        new IndentPrinter());
    String output = prettyPrinter.prettyprint(cliteral);
    
    result = parser.parseCharLiteral(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(cliteral.deepEquals(result.get()));
  }
  
  @Test
  public void testStringLiteral() throws IOException {
    TestStringLiteralsParser parser = new TestStringLiteralsParser();
    Optional<ASTStringLiteral> result = parser
        .parseStringLiteral(new StringReader("\"Text mit 893\""));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTStringLiteral sliteral = result.get();
    
    StringLiteralsPrettyPrinter prettyPrinter = new StringLiteralsPrettyPrinter(
        new IndentPrinter());
    String output = prettyPrinter.prettyprint(sliteral);
    result = parser.parseStringLiteral(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(sliteral.deepEquals(result.get()));
  }
  
}
