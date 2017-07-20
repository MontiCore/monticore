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

package mc.feature.mcenum;

import mc.GeneratorIntegrationsTest;
/*
import mc.feature.mcenum._ast.ASTA;
import mc.feature.mcenum._ast.ASTB;
import mc.feature.mcenum._ast.ASTConstantsEnums;
import mc.feature.mcenum._ast.E;
import mc.feature.mcenum._ast.F;
import mc.feature.mcenum._ast.G;
import mc.feature.mcenum._parser.AParser;
import mc.feature.mcenum._parser.BParser;
*/
public class EnumTest extends GeneratorIntegrationsTest {
  // TODO SO <- GV: transformation foe MC-enums
  /*
  @Test
  public void testa() throws IOException {
    
    AParser p = new AParser();
    
    Optional<ASTA> optAst = p.parse(new StringReader("++++WORD"));
    assertTrue(optAst.isPresent());
    ASTA ast = optAst.get();
    assertEquals(true, ast.isA());
    assertEquals(true, ast.getE() == E.PLUS);
    assertEquals(true, ast.getG() == G.PLUS);
    assertEquals(true, ast.getF() == F.PLUS);
    assertEquals(true, ast.getF().intValue() == ASTConstantsEnums.PLUS);
    assertEquals(true, ast.getF().ordinal() == 0);
    assertEquals(true, ast.getF().name() == "PLUS");
  }
  
   @Test
  public void testB() throws IOException {
    
    BParser p = new BParser();
    
    Optional<ASTB> optAst = p.parse(new StringReader("++,++"));
    assertTrue(optAst.isPresent());
    ASTB ast = optAst.get();
    assertEquals(true, ast.getE().get(0) == E.PLUS);
    assertEquals(true, ast.getE().get(0).intValue() == ASTConstantsEnums.PLUS);
    assertEquals(2, ast.getE().size());
    assertEquals(true, ast.getF().get(0) == F.PLUS);
    assertEquals(true, ast.getF().get(0).intValue() == ASTConstantsEnums.PLUS);
    assertEquals(2, ast.getF().size());
    
  }
  
   @Test
  public void testB2() throws IOException {
    
    BParser p = new BParser();
    
    Optional<ASTB> optAst = p.parse(new StringReader("++,#+"));
    assertTrue(optAst.isPresent());
    ASTB ast = optAst.get();
    
    assertEquals(true, ast.getE().get(0) == E.PLUS);
    assertEquals(2, ast.getE().size());
    assertEquals(true, ast.getF().get(0).ordinal() == 0);
    assertEquals(2, ast.getF().size());
    assertEquals(ast.getF().get(0), ast.getF().get(1));
    assertEquals(true, ast.getF().get(0) == F.PLUS);
    
  }
  
   @Test
  public void testB3() throws IOException {
    
    BParser p = new BParser();
    
    Optional<ASTB> optAst = p.parse(new StringReader("++,#-"));
    assertTrue(optAst.isPresent());
    ASTB ast = optAst.get();
   
    assertEquals(2, ast.getE().size());
    assertEquals(true, ast.getE().get(0) == E.PLUS);
    assertEquals(true, ast.getE().get(1) == E.PLUS);
    
    assertEquals(2, ast.getF().size());
    assertEquals(true, ast.getF().get(0) == F.PLUS);
    assertEquals(true, ast.getF().get(1) == F.MINUS);
    
  }*/
}
