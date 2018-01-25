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
import de.monticore.completeness._ast.ASTCompleteness;
import de.monticore.prettyprint.CompletenessPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.testcompleteness._parser.TestCompletenessParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;

/**
 * @author npichler
 */

public class CompletenessPrettyPrinterTest {
  
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
  public void testCompleteness() throws IOException {
    TestCompletenessParser parser = new TestCompletenessParser();
    Optional<ASTCompleteness> result = parser.parseCompleteness(new StringReader("(c)"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTCompleteness completeness = result.get();
    
    CompletenessPrettyPrinter prettyPrinter = new CompletenessPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(completeness);
    
    result = parser.parseCompleteness(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(completeness.deepEquals(result.get()));
  }
  
  @Test
  public void testIncompleteness() throws IOException {
    TestCompletenessParser parser = new TestCompletenessParser();
    Optional<ASTCompleteness> result = parser.parseCompleteness(new StringReader("(...)"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTCompleteness completeness = result.get();
    
    CompletenessPrettyPrinter prettyPrinter = new CompletenessPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(completeness);
    
    result = parser.parseCompleteness(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(completeness.deepEquals(result.get()));
  }
  
  @Test
  public void testRightCompleteness() throws IOException {
    TestCompletenessParser parser = new TestCompletenessParser();
    Optional<ASTCompleteness> result = parser.parseCompleteness(new StringReader("(...,c)"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTCompleteness completeness = result.get();
    
    CompletenessPrettyPrinter prettyPrinter = new CompletenessPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(completeness);
    
    result = parser.parseCompleteness(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(completeness.deepEquals(result.get()));
  }
  
  @Test
  public void testLeftCompleteness() throws IOException {
    TestCompletenessParser parser = new TestCompletenessParser();
    Optional<ASTCompleteness> result = parser.parseCompleteness(new StringReader("(c,...)"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTCompleteness completeness = result.get();
    
    CompletenessPrettyPrinter prettyPrinter = new CompletenessPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(completeness);
    
    result = parser.parseCompleteness(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(completeness.deepEquals(result.get()));
  }
}
