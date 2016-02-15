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

package mc.feature.embedding;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.embedding.outer.embedded._parser.EmbeddedParser;

public class EmbedTest extends GeneratorIntegrationsTest {
  
  @Test
  public void test() throws IOException {
    
    EmbeddedParser parser = new EmbeddedParser();
    parser.parseStart(new StringReader("a a a"));
    
    assertEquals(false, parser.hasErrors());
  }
  
  @Test
  public void test2_a() throws IOException {
    
    EmbeddedParser parser = new EmbeddedParser();
    parser.parseStart(new StringReader("a x a"));
    
    assertEquals(false, parser.hasErrors());
  }
  
  @Test
  public void test2_b() throws IOException {
    
    EmbeddedParser parser = new EmbeddedParser();
    parser.parseStart2(new StringReader("a x a"));
    
    assertEquals(false, parser.hasErrors());
  }
  
  @Test
  public void test3() throws IOException {
    
    EmbeddedParser parser = new EmbeddedParser();
    parser.parseStart2(new StringReader("a a x a a"));
    
    assertEquals(true, parser.hasErrors());
  }
  
  @Test
  public void test4() throws IOException {
    
    EmbeddedParser parser = new EmbeddedParser();
    parser.parseStart3(new StringReader("b x"));
    
    assertEquals(false, parser.hasErrors());
  }
  
}
