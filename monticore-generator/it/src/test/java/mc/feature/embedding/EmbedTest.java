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
import groovyjarjarantlr.TokenStreamException;

import java.io.IOException;
import java.io.StringReader;

import mc.GeneratorIntegrationsTest;
import mc.feature.embedding.outer.embedded._parser.EmbeddedParserFactory;
import mc.feature.embedding.outer.embedded._parser.Start2MCParser;
import mc.feature.embedding.outer.embedded._parser.Start3MCParser;
import mc.feature.embedding.outer.embedded._parser.StartMCParser;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

public class EmbedTest extends GeneratorIntegrationsTest {
  
  @Test
  public void test() throws RecognitionException, TokenStreamException, IOException {
    
    StartMCParser parser = EmbeddedParserFactory.createStartMCParser();
    parser.parse(new StringReader("a a a"));
    
    assertEquals(false, parser.hasErrors());
  }
  
  @Test
  public void test2_a() throws RecognitionException, TokenStreamException, IOException {
    
    StartMCParser parser = EmbeddedParserFactory.createStartMCParser();
    parser.parse(new StringReader("a x a"));
    
    assertEquals(false, parser.hasErrors());
  }
  
  @Test
  public void test2_b() throws RecognitionException, TokenStreamException, IOException {
    
    Start2MCParser parser = EmbeddedParserFactory.createStart2MCParser();
    parser.parse(new StringReader("a x a"));
    
    assertEquals(false, parser.hasErrors());
  }
  
  @Test
  public void test3() throws RecognitionException, TokenStreamException, IOException {
    
    Start2MCParser parser = EmbeddedParserFactory.createStart2MCParser();
    parser.parse(new StringReader("a a x a a"));
    
    assertEquals(true, parser.hasErrors());
  }
  
  @Test
  public void test4() throws RecognitionException, TokenStreamException, IOException {
    
    Start3MCParser parser = EmbeddedParserFactory.createStart3MCParser();
    parser.parse(new StringReader("b x"));
    
    assertEquals(false, parser.hasErrors());
  }
  
}
