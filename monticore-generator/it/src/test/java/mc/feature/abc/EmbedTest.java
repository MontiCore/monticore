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

package mc.feature.abc;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.abc.realabc._parser.RealABCParser;

public class EmbedTest extends GeneratorIntegrationsTest {
  
  @Test
  public void test() throws IOException {
    
    RealABCParser p = parse("a b c");
    
    assertEquals(false, p.hasErrors());
  }
  
  @Test
  public void testb() throws IOException {
    
    RealABCParser p = parse("a b");
    
    assertEquals(false, p.hasErrors());
  }
  
  @Test
  public void testc() throws IOException {
    
    RealABCParser p = parse("a a a b b b c c c");
    
    assertEquals(false, p.hasErrors());
  }
  
  @Test
  public void testd() throws IOException {
    
    RealABCParser p = parse("a b c c");
    
    assertEquals(false, p.hasErrors());
  }
  
  private RealABCParser parse(String in) throws IOException {
    RealABCParser parser = new  RealABCParser();
    parser.parseS(new StringReader(in));

    return parser;
  }
  
}
