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

package mc.feature.listrule;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.listrule.listrule._parser.ListRuleParser;

public class ListRuleTest extends GeneratorIntegrationsTest {

  @Test
  public void testParent1() throws IOException {
    StringReader s = new StringReader(
        "P1 a, P1 b");
    ListRuleParser p = new ListRuleParser();
    p.parseParent(s);

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
    ListRuleParser p = new ListRuleParser();
    p.parseParent2(s);

    assertEquals(false, p.hasErrors());
  }

  @Test
  public void testParent3() throws IOException {
    StringReader s = new StringReader(
        "P3 a, P3 b");
    ListRuleParser p = new ListRuleParser();
    p.parseParent3(s);

    assertEquals(false, p.hasErrors());
  }

  @Test
  public void testParent4() throws IOException {
    StringReader s = new StringReader(
        "P4 a, P4 b");
    ListRuleParser p = new ListRuleParser();
    p.parseParent4(s);

    assertEquals(false, p.hasErrors());

    // Empty lists are allowed
    s = new StringReader("");
    p.parseParent4(s);

    assertEquals(false, p.hasErrors());
  }


  @Test
  public void testParent6() throws IOException {
    StringReader s = new StringReader(
        "a, P1");
    ListRuleParser p = new ListRuleParser();
    p.parseParent6(s);

    assertEquals(false, p.hasErrors());
  }}
