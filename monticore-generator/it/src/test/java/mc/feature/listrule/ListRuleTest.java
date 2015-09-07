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

package mc.feature.listrule;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import mc.GeneratorIntegrationsTest;
import mc.feature.listrule.listrule._parser.ListRuleParserFactory;
import mc.feature.listrule.listrule._parser.Parent2MCParser;
import mc.feature.listrule.listrule._parser.Parent3MCParser;
import mc.feature.listrule.listrule._parser.Parent4MCParser;
import mc.feature.listrule.listrule._parser.Parent5MCParser;
import mc.feature.listrule.listrule._parser.Parent6MCParser;
import mc.feature.listrule.listrule._parser.ParentMCParser;

import org.junit.Test;

public class ListRuleTest extends GeneratorIntegrationsTest {
  
  @Test
  public void testParent1() throws IOException {
    StringReader s = new StringReader(
        "P1 a, P1 b");
    ParentMCParser p = ListRuleParserFactory.createParentMCParser();
    p.parse(s);
    
    assertEquals(false, p.hasErrors());
    
    // Empty lists are NOT allowed
    s = new StringReader("");
    p.parse(s);
    
    assertEquals(true, p.hasErrors());
  }

  @Test
  public void testParent2() throws IOException {
    StringReader s = new StringReader(
        "Parent2 P2 a, P2 b Parent2");
    Parent2MCParser p = ListRuleParserFactory.createParent2MCParser();
    p.parse(s);
    
    assertEquals(false, p.hasErrors());
  }

  @Test
  public void testParent3() throws IOException {
    StringReader s = new StringReader(
        "P3 a, P3 b");
    Parent3MCParser p = ListRuleParserFactory.createParent3MCParser();
    p.parse(s);
    
    assertEquals(false, p.hasErrors());
  }
 
  @Test
  public void testParent4() throws IOException {
    StringReader s = new StringReader(
        "P4 a, P4 b");
    Parent4MCParser p = ListRuleParserFactory.createParent4MCParser();
    p.parse(s);
    
    assertEquals(false, p.hasErrors());
    
    // Empty lists are allowed
    s = new StringReader("");
    p.parse(s);
    
    assertEquals(false, p.hasErrors());
  }

  @Test
  public void testParent5() throws IOException {
    StringReader s = new StringReader(
        "P5 a, P5 b");
    Parent5MCParser p = ListRuleParserFactory.createParent5MCParser();
    p.parse(s);
    
    assertEquals(false, p.hasErrors());
  }

  @Test
  public void testParent6() throws IOException {
    StringReader s = new StringReader(
        "a, P1");
    Parent6MCParser p = ListRuleParserFactory.createParent6MCParser();
    p.parse(s);
    
    assertEquals(false, p.hasErrors());
  }}
