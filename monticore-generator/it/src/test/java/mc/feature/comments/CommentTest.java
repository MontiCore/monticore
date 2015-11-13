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

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.featuredsl._ast.ASTAutomaton;
import mc.feature.featuredsl._ast.ASTConstants;
import mc.feature.featuredsl._ast.ASTConstantsFeatureDSL;
import mc.feature.featuredsl._ast.ASTSpices1;
import mc.feature.featuredsl._ast.ASTSpices2;
import mc.feature.featuredsl._parser.FeatureDSLParser;

public class CommentTest extends GeneratorIntegrationsTest {
  
  @Test
  public void testConstants() throws org.antlr.v4.runtime.RecognitionException, IOException  {
    StringReader s = new StringReader(
        "// Test \n /*Second*/ automaton a { // First Constant 1\n constants public ;// First Constant 2\n /*Second Constant*/ constants +; constants private; spices1 garlic pepper;	spices2 none;}");
        
    FeatureDSLParser cp = new FeatureDSLParser();
    
    java.util.Optional<ASTAutomaton> optAst = cp.parseAutomaton(s);
    ASTAutomaton ast = optAst.get();
    
    // Parsing
    assertEquals(false, cp.hasErrors());
    assertEquals("a", ast.getName());
    
    assertEquals(true, ((ASTConstants) ast.getWired().get(0)).isPubblic());
    assertEquals(false, ((ASTConstants) ast.getWired().get(0)).isPrivate());
    
    assertEquals(true, ((ASTConstants) ast.getWired().get(1)).isPubblic());
    assertEquals(false, ((ASTConstants) ast.getWired().get(1)).isPrivate());
    
    assertEquals(false, ((ASTConstants) ast.getWired().get(2)).isPubblic());
    assertEquals(true, ((ASTConstants) ast.getWired().get(2)).isPrivate());
    
    assertEquals(true, ((ASTSpices1) ast.getWired().get(3)).isCarlique());
    assertEquals(true, ((ASTSpices1) ast.getWired().get(3)).isPepper());
    
    assertEquals(ASTConstantsFeatureDSL.NONE, ((ASTSpices2) ((ASTAutomaton) ast).getWired().get(4)).getSpicelevel());
    
    // TODO Antlr: Will man das Newline am Ende des Kommentars haben?
    assertEquals("// Test ", ast.get_PreComments().get(0).getText());
    assertEquals("/*Second*/", ast.get_PreComments().get(1).getText());
    assertEquals("// First Constant 1", ast.getWired().get(0).get_PreComments().get(0).getText());
    assertEquals("// First Constant 2",  ast.getWired().get(0).get_PostComments().get(0).getText());
    assertEquals("/*Second Constant*/", ast.getWired().get(1).get_PreComments().get(0).getText());
    
  }
  
}
