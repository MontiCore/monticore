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

package mc.feature.comments;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import de.monticore.ast.ASTNode;
import mc.GeneratorIntegrationsTest;
import mc.feature.comments.commenttest._ast.ASTStart;
import mc.feature.comments.commenttest._parser.CommentTestParser;

public class CommentsTest extends GeneratorIntegrationsTest {
  
  /**
   * This Test tests if the comments are assigned correctly. 
   * 
   * @throws IOException 
   */
  @Test
  public void testComment() throws IOException {
    StringReader r = new StringReader("start /* comment 1 */ test a // comment 2 \n test b");
    
    CommentTestParser p = new CommentTestParser();    
    java.util.Optional<ASTStart> optAst =  p.parseStart(r);
    assertTrue(optAst.isPresent());
    ASTStart ast = optAst.get();
    assertEquals(false, p.hasErrors());
    assertEquals(1, ast.getAs().size());
    assertEquals(1, ast.getBs().size());
    assertEquals(1, ((ASTNode) ast.getAs().get(0)).get_PreComments().size());
    assertEquals(1, ((ASTNode) ast.getAs().get(0)).get_PostComments().size());
    assertEquals(0, ((ASTNode) ast.getBs().get(0)).get_PreComments().size());
  }
}
