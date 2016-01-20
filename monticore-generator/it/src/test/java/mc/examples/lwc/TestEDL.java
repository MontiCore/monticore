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

package mc.examples.lwc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Optional;

import org.junit.Test;

import com.google.common.collect.Lists;

import mc.GeneratorIntegrationsTest;
import mc.examples.lwc.edl.edl._ast.ASTEDLCompilationUnit;
import mc.examples.lwc.edl.edl._ast.ASTEntity;
import mc.examples.lwc.edl.edl._ast.ASTIntLiteral;
import mc.examples.lwc.edl.edl._ast.ASTQualifiedName;
import mc.examples.lwc.edl.edl._ast.ASTReferenceType;
import mc.examples.lwc.edl.edl._ast.ASTStringLiteral;
import mc.examples.lwc.edl.edl._parser.EDLParser;

public class TestEDL extends GeneratorIntegrationsTest {
  
  @Test
  public void testParser() throws IOException {
    EDLParser parser = new EDLParser();
    Optional<ASTEDLCompilationUnit> ast = parser
        .parseEDLCompilationUnit("src/test/resources/examples/lwc/edl/Car.edl");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertNotNull(ast.get().getEntity());
    
    ASTEntity entity = ast.get().getEntity();
    
    assertEquals(entity.getName(), "Car");
    assertEquals(entity.getPropertys().size(), 7);
    
    assertEquals(entity.getPropertys().get(0).getName(), "brand");
    assertTrue(entity.getPropertys().get(0).getType()
        .deepEquals(ASTStringLiteral.getBuilder().build()));
    
    assertEquals(entity.getPropertys().get(1).getName(), "model");
    assertTrue(entity.getPropertys().get(1).getType()
        .deepEquals(ASTStringLiteral.getBuilder().build()));
    
    assertEquals(entity.getPropertys().get(2).getName(), "price");
    assertTrue(entity.getPropertys().get(2).getType()
        .deepEquals(ASTIntLiteral.getBuilder().build()));
    
    assertEquals(entity.getPropertys().get(3).getName(), "age");
    assertTrue(entity.getPropertys().get(3).getType()
        .deepEquals(ASTIntLiteral.getBuilder().build()));
    
    assertEquals(entity.getPropertys().get(4).getName(), "doors");
    assertTrue(entity.getPropertys().get(4).getType()
        .deepEquals(ASTIntLiteral.getBuilder().build()));
    
    assertEquals(entity.getPropertys().get(5).getName(), "myself");
    assertTrue(entity
        .getPropertys()
        .get(5)
        .getType()
        .deepEquals(
            ASTReferenceType
                .getBuilder()
                .qualifiedName(
                    ASTQualifiedName.getBuilder().names(Lists.newArrayList("Car")).build()).build()));
    
    assertEquals(entity.getPropertys().get(6).getName(), "owner");
    assertTrue(entity
        .getPropertys()
        .get(6)
        .getType()
        .deepEquals(
            ASTReferenceType
                .getBuilder()
                .qualifiedName(
                    ASTQualifiedName.getBuilder().names(Lists.newArrayList("lwc", "edl", "Person"))
                        .build()).build()));
    
  }
  
}
