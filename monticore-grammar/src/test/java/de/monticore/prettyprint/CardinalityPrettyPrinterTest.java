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
import de.monticore.cardinality._ast.ASTCardinality;
import de.monticore.prettyprint.CardinalityPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.testcardinality._parser.TestCardinalityParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;

/**
 * @author npichler
 */

public class CardinalityPrettyPrinterTest {
  
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
  public void testCardinality1() throws IOException {
    TestCardinalityParser parser = new TestCardinalityParser();
    Optional<ASTCardinality> result = parser.parseCardinality(new StringReader("[*]"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTCardinality cardinality = result.get();
    
    CardinalityPrettyPrinter prettyPrinter = new CardinalityPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(cardinality);
    
    result = parser.parseCardinality(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(cardinality.deepEquals(result.get()));
  }
  
  @Test
  public void testCardinality2() throws IOException {
    TestCardinalityParser parser = new TestCardinalityParser();
    Optional<ASTCardinality> result = parser.parseCardinality(new StringReader("[5..9]"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTCardinality cardinality = result.get();
    
    CardinalityPrettyPrinter prettyPrinter = new CardinalityPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(cardinality);
    
    result = parser.parseCardinality(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(cardinality.deepEquals(result.get()));
  }
  
  @Test
  public void testCardinality3() throws IOException {
    TestCardinalityParser parser = new TestCardinalityParser();
    Optional<ASTCardinality> result = parser.parseCardinality(new StringReader("[6..*]"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTCardinality cardinality = result.get();
    
    CardinalityPrettyPrinter prettyPrinter = new CardinalityPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(cardinality);
    
    result = parser.parseCardinality(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(cardinality.deepEquals(result.get()));
  }
}
