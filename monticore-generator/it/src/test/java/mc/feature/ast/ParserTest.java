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

package mc.feature.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.Optional;

import mc.GeneratorIntegrationsTest;
import mc.feature.featuredsl._ast.ASTA;
import mc.feature.featuredsl._ast.ASTAList;
import mc.feature.featuredsl._ast.ASTAutomaton;
import mc.feature.featuredsl._ast.ASTB;
import mc.feature.featuredsl._ast.ASTC;
import mc.feature.featuredsl._ast.ASTComplexname;
import mc.feature.featuredsl._ast.ASTConstants;
import mc.feature.featuredsl._ast.ASTConstantsFeatureDSL;
import mc.feature.featuredsl._ast.ASTSpices1;
import mc.feature.featuredsl._ast.ASTSpices2;
import mc.feature.featuredsl._parser.AutomatonMCParser;
import mc.feature.featuredsl._parser.BMCParser;
import mc.feature.featuredsl._parser.CMCParser;
import mc.feature.featuredsl._parser.ComplexnameMCParser;
import mc.feature.featuredsl._parser.FeatureDSLParserFactory;

import org.junit.Test;

public class ParserTest extends GeneratorIntegrationsTest {
  
  @Test
  public void testConstants() throws IOException {
    StringReader s = new StringReader(
        "automaton a { constants public; constants +; constants private; spices1 garlic pepper;	spices2 none;}");
    
    AutomatonMCParser p = FeatureDSLParserFactory.createAutomatonMCParser();
    Optional<ASTAutomaton> opt = p.parse(s);
    assertTrue(opt.isPresent());
    ASTAutomaton ast = opt.get();
    
    assertEquals(false, p.hasErrors());
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
    
  }
  
  @Test
  public void testConstantsParseError() throws IOException {
    StringReader s = new StringReader(
        "automaton a { spices2 ;}");
    
    // Ignore std.err
    System.setOut(new PrintStream(new ByteArrayOutputStream()));
    
    AutomatonMCParser p = FeatureDSLParserFactory.createAutomatonMCParser();
    p.parse(s);
    
    assertEquals(true, p.hasErrors());
    
  }
  
  /*  Grammar:  B: A:A (B:A)*; 
   * 
   * In a previous version A and B were both lists (<- bug)
   * */
  @Test
  public void testListError() throws IOException {
    
    StringReader s = new StringReader(
        "private / private / private /");
    
    // Ignore std.err
    System.setErr(new PrintStream(new ByteArrayOutputStream()));
    
    BMCParser p = FeatureDSLParserFactory.createBMCParser();
    Optional<ASTB> ast = p.parse(s);
    
    assertEquals(false, p.hasErrors());
    assertTrue(ast.isPresent());
    assertEquals(true, ast.get().getA() instanceof ASTA);
    assertEquals(true, ast.get().getB() instanceof ASTAList);
    
  }
  
  /*  Grammar:  B: A:A (A:A)*; 
   * 
   *  A has to be of type ASTAList
   * */
  @Test
  public void testListError2() throws IOException {
    
    StringReader s = new StringReader(
        "private / private / private /");
    
    // Ignore std.err
    System.setErr(new PrintStream(new ByteArrayOutputStream()));
    
    CMCParser p = FeatureDSLParserFactory.createCMCParser();
    Optional<ASTC> ast = p.parse(s);
    
    assertTrue(ast.isPresent());
    assertEquals(false, p.hasErrors());
    assertEquals(true, ast.get().getA() instanceof ASTAList);
    
  }
  
  /*  Grammar: 
   * 
   *  A has to be of type ASTAList
   * */
  @Test
  public void testListError3() throws IOException {
    
    StringReader s = new StringReader(
        "private / private / private /");
    
    // Ignore std.err
    System.setErr(new PrintStream(new ByteArrayOutputStream()));
    
    ComplexnameMCParser p = FeatureDSLParserFactory.createComplexnameMCParser();
    Optional<ASTComplexname> ast = p.parse(s);
    
    assertFalse(ast.isPresent());    
  }
  
}
