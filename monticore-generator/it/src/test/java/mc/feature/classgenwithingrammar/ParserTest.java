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

package mc.feature.classgenwithingrammar;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.classgenwithingrammar.type._parser.TypeParser;

public class ParserTest extends GeneratorIntegrationsTest {

  
  // Test that one Welt is too much
  @Test
  public void test() throws IOException {
        
    boolean hasError = parse("Hallo Hallo Hallo Welt ");
    assertTrue(hasError);
  }
  
  // Test that too many Hallo and Welt are detected in one go
  @Test
  public void test2() throws IOException {
        
    boolean hasError = parse("Hallo Hallo Hallo Hallo Welt ");
    assertTrue(hasError);
    
  }
  
  // Tests that String is ok
  @Test
  public void test3() throws IOException {
        
    boolean hasError = parse("Hallo Hallo Hallo ");
    
    assertFalse(hasError);
    
  }
  
  // Test that one Welt is too much
  @Test
  public void testl() throws IOException {
        
    boolean hasError = parse2("Hall Hall Hall \"Wel\" ");
    
    assertTrue(hasError);

  }
  
  // Test that too many Hallo and Welt are detected in one go
  @Test
  public void testl2() throws IOException {
        
    boolean hasError = parse2("Hall Hall Hall Hall \"Wel\" ");
    
    assertTrue(hasError);
    
  }
  
  // Tests that String is ok
  @Test
  public void testl3() throws IOException {
        
    boolean hasError = parse2("Hall Hall Hall ");
    
    assertFalse(hasError);
    
  }
  
  private boolean parse( String input) throws IOException {
    StringReader s = new StringReader(input);    
    TypeParser parser = new TypeParser();
            
    parser.parseType(s);
    return parser.hasErrors();
  }
  
  private boolean parse2(String input) throws IOException {
    StringReader s = new StringReader(input);
    
    TypeParser parser = new TypeParser();
    
    parser.parseType2(s);
    return parser.hasErrors();

  }
}
