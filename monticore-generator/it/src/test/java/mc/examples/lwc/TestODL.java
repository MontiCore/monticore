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
import mc.examples.lwc.odl.odl._ast.ASTDate;
import mc.examples.lwc.odl.odl._ast.ASTDateValue;
import mc.examples.lwc.odl.odl._ast.ASTInstances;
import mc.examples.lwc.odl.odl._ast.ASTIntValue;
import mc.examples.lwc.odl.odl._ast.ASTODLCompilationUnit;
import mc.examples.lwc.odl.odl._ast.ASTQualifiedName;
import mc.examples.lwc.odl.odl._ast.ASTReferenceValue;
import mc.examples.lwc.odl.odl._ast.ASTStringValue;
import mc.examples.lwc.odl.odl._parser.ODLParser;

public class TestODL extends GeneratorIntegrationsTest {
  
  @Test
  public void testParser() throws IOException {
    ODLParser parser = new ODLParser();
    Optional<ASTODLCompilationUnit> ast = parser
        .parseODLCompilationUnit("src/test/resources/examples/lwc/odl/MyWorld.odl");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    
    ASTInstances instances = ast.get().getInstances();
    assertNotNull(instances);
    
    assertEquals(instances.getName(), "MyWorld");
    assertEquals(instances.getObjects().size(), 2);
    
    assertEquals(instances.getObjects().get(0).getName(), "person");
    assertTrue(instances.getObjects().get(0).getType().deepEquals(
        ASTQualifiedName.getBuilder().names(Lists.newArrayList("Person")).build()));
    
    assertEquals(instances.getObjects().get(0).getAssignments().size(), 4);
    assertEquals(instances.getObjects().get(0).getAssignments().get(0).getName(), "birthday");
    assertTrue(instances
        .getObjects()
        .get(0)
        .getAssignments()
        .get(0)
        .getValue()
        .deepEquals(
            ASTDateValue.getBuilder()
                .date(ASTDate.getBuilder().day("01").month("01").year("1999").build()).build()));
    
    assertEquals(instances.getObjects().get(0).getAssignments().get(1).getName(), "name");
    assertTrue(instances
        .getObjects()
        .get(0)
        .getAssignments()
        .get(1)
        .getValue()
        .deepEquals(
            ASTStringValue.getBuilder()
                .sTRING("alice").build()));
    
    assertEquals(instances.getObjects().get(0).getAssignments().get(2).getName(), "id");
    assertTrue(instances
        .getObjects()
        .get(0)
        .getAssignments()
        .get(2)
        .getValue()
        .deepEquals(
            ASTIntValue.getBuilder()
                .iNT("1").build()));
    
    assertEquals(instances.getObjects().get(0).getAssignments().get(3).getName(), "car");
    assertTrue(instances
        .getObjects()
        .get(0)
        .getAssignments()
        .get(3)
        .getValue()
        .deepEquals(
            ASTReferenceValue.getBuilder()
                .name("car").build()));
    
    assertEquals(instances.getObjects().get(1).getName(), "car");
    assertTrue(instances.getObjects().get(1).getType().deepEquals(
        ASTQualifiedName.getBuilder().names(Lists.newArrayList("lwc", "edl", "Car")).build()));
  }
  
}
