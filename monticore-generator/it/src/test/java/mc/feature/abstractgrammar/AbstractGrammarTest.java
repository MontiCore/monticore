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

package mc.feature.abstractgrammar;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;

import mc.GeneratorIntegrationsTest;
import mc.feature.abstractgrammar.abstractgrammar._ast.ASTUseAbstract;
import mc.feature.abstractgrammar.abstractgrammar._ast.ASTUseUnterface;
import mc.feature.abstractgrammar.implementation._ast.ASTB;
import mc.feature.abstractgrammar.implementation._ast.ASTC;
import mc.feature.abstractgrammar.implementation._parser.ImplementationParserFactory;
import mc.feature.abstractgrammar.implementation._parser.UseAbstractMCParser;
import mc.feature.abstractgrammar.implementation._parser.UseUnterfaceMCParser;

import org.junit.Test;

public class AbstractGrammarTest extends GeneratorIntegrationsTest {
  
  @Test
  public void testRefInterface() throws IOException {
    
    UseUnterfaceMCParser p = ImplementationParserFactory.createUseUnterfaceMCParser();
    java.util.Optional<ASTUseUnterface> ast = p.parse(new StringReader("use impl myimplinterface"));
        
    assertTrue(ast.isPresent());
    assertFalse(p.hasErrors());
    assertTrue(ast.get().getII() instanceof ASTB);
  }
  
  public void testRefAbstractRule() throws IOException {
    
    UseAbstractMCParser p = ImplementationParserFactory.createUseAbstractMCParser();
    java.util.Optional<ASTUseAbstract> ast = p.parse(new StringReader("use ext myextabstract"));
    
    assertTrue(ast.isPresent());
    assertFalse(p.hasErrors());
    assertTrue(ast.get().getAA() instanceof ASTC);
  }
}
