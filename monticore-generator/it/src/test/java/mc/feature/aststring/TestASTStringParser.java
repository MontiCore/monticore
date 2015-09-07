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

package mc.feature.aststring;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;

import mc.GeneratorIntegrationsTest;
import mc.feature.aststring.aststring._ast.ASTStart;
import mc.feature.aststring.aststring._parser.AststringParserFactory;
import mc.feature.aststring.aststring._parser.StartMCParser;

import org.junit.Test;

public class TestASTStringParser extends GeneratorIntegrationsTest {
  
  @Test
  public void testMCParser() throws IOException {
    
    StringReader s = new StringReader(
        "start ah be ce , oh pe qu , x.y.z , de eh ef");
    
    StartMCParser p = AststringParserFactory.createStartMCParser();
    java.util.Optional<ASTStart> opt = p.parse(s);
    assertTrue(opt.isPresent());
    ASTStart ast = opt.get();
    
    assertEquals(false, p.hasErrors());
    
    // Test parsing
    assertEquals("ah", ast.getA().get(0));
    assertEquals("be", ast.getA().get(1));
    assertEquals("ce", ast.getA().get(2));
    assertEquals("oh", ast.getB().get(0));
    assertEquals("pe", ast.getB().get(1));
    assertEquals("qu", ast.getB().get(2));
    assertEquals("x", ast.getC().get(0));
    assertEquals("y", ast.getC().get(1));
    assertEquals("z", ast.getC().get(2));
    assertEquals("de", ast.getD().get(0));
    assertEquals("eh", ast.getD().get(1));
    
    // Test toString method
    assertEquals("ef", ast.getD().get(2).toString());
    
  }
  
}
