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

package mc.feature.abstractprod;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;

import mc.GeneratorIntegrationsTest;
import mc.feature.abstractprod.abstractprod._ast.ASTA;
import mc.feature.abstractprod.abstractprod._ast.ASTB;
import mc.feature.abstractprod.abstractprod._ast.ASTC;
import mc.feature.abstractprod.abstractprod._parser.AMCParser;
import mc.feature.abstractprod.abstractprod._parser.AbstractProdParserFactory;

import org.junit.Test;

public class AbstractProdTest extends GeneratorIntegrationsTest {
  
  @Test
  public void testb() throws IOException {
    
    AMCParser p = AbstractProdParserFactory.createAMCParser();
    java.util.Optional<ASTA> ast = p.parse(new StringReader("b"));
    
    assertTrue(ast.isPresent());
    assertTrue(ast.get() instanceof ASTB);
    assertFalse(p.hasErrors());
    
  }
  
  @Test
  public void testc() throws IOException {
    
    AMCParser p = AbstractProdParserFactory.createAMCParser();
    java.util.Optional<ASTA> ast = p.parse(new StringReader("c"));

    assertTrue(ast.isPresent());
    assertTrue(ast.get() instanceof ASTC);
    assertFalse(p.hasErrors());
    
  }
}
