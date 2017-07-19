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

package mc.feature.comments;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.comments.commenttypestest._parser.CommentTypesTestParser;

public class CommentTypesTest extends GeneratorIntegrationsTest {
  
  /**
   * This Test tests if xml comments are parsed correctly.
   * 
   * @throws IOException 
   */
  @Test
  public void testXMLComment() throws IOException  {
    StringReader r = new StringReader("start <!-- comment \n --> marita");
    
    CommentTypesTestParser p = new CommentTypesTestParser();    
    p.parseCStart(r);
    
    assertEquals(false, p.hasErrors());
  }
  
  /**
   * This Test tests if xml comments with including "--" are parsed correctly.
   * 
   */
  @Test
  public void testCComment_With__() throws IOException  {
    StringReader r = new StringReader("start <!-- -- --> marita");
    
    CommentTypesTestParser p = new CommentTypesTestParser();    
    p.parseCStart(r);
    
    assertEquals(false, p.hasErrors());
  }
  
  /**
   * This Test tests if tex comments are parsed correctly.
   * 
   */
  @Test
  public void testTexComment() throws IOException {
    StringReader r = new StringReader("start % comment\n  marita");
    
    CommentTypesTestParser p = new CommentTypesTestParser();    
    p.parseCStart(r);
    
    assertEquals(false, p.hasErrors());
  }
  
  /**
   * This Test tests if freemarker comments are parsed correctly.
   * 
   */
  @Test
  public void testFreeMarkerComment() throws IOException {
    StringReader r = new StringReader("start <#-- comment \n --> marita");
    
    CommentTypesTestParser p = new CommentTypesTestParser();    
    p.parseCStart(r);
    
    assertEquals(false, p.hasErrors());
  }
  
  /**
   * This Test tests if hash comments are parsed correctly.
   * 
   */
  @Test
  public void testHashComment() throws IOException {
    StringReader r = new StringReader("start # comment \n marita");
    
    CommentTypesTestParser p = new CommentTypesTestParser();    
    p.parseCStart(r);
    
    assertEquals(false, p.hasErrors());
  }
  
}
