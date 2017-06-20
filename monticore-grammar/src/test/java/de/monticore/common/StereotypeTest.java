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

package de.monticore.common;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.common.common._ast.ASTStereotype;
import de.monticore.common.testcommon._parser.TestCommonParser;
import de.se_rwth.commons.logging.Log;

/**
 * @author Marita Breuer
 */
public class StereotypeTest {
  
  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }
  
  private ASTStereotype parseStereotype(String s) throws IOException {
    TestCommonParser parser = new TestCommonParser();
    Optional<ASTStereotype> ast = parser.parseStereotype(new StringReader(s));
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    return ast.get();
  }
  
  @Test
  public void parseStereotype() {
    try {
      parseStereotype("<<s1=\"S1\">>");
      parseStereotype("<<s1=\"S1\", s2>>");
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
  
  @Test
  public void parseNegativeStereotype() {
    try {
      TestCommonParser parser = new TestCommonParser();
      parser.parseStereotype(new StringReader("<<s1> >"));
      assertTrue(parser.hasErrors());
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
  
  @Test
  public void parseGenericType() {
    // Check if handling of ">>" in generic tpyes is correct
    try {
      TestCommonParser parser = new TestCommonParser();
      parser.parseType(new StringReader("List<List<String>>"));
      assertFalse(parser.hasErrors());
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
  
}
