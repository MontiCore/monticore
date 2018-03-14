/* (c) https://github.com/MontiCore/monticore */

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
import mc.examples.lwc.odl.odl._ast.ASTInstances;
import mc.examples.lwc.odl.odl._ast.ASTODLCompilationUnit;
import mc.examples.lwc.odl.odl._ast.ODLMill;
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
    assertEquals(instances.getObjectList().size(), 2);
    
    assertEquals(instances.getObjectList().get(0).getName(), "person");
    assertTrue(instances.getObjectList().get(0).getType().deepEquals(
        ODLMill.qualifiedNameBuilder().setNameList(Lists.newArrayList("Person")).build()));
    
    assertEquals(instances.getObjectList().get(0).getAssignmentList().size(), 4);
    assertEquals(instances.getObjectList().get(0).getAssignmentList().get(0).getName(), "birthday");
    assertTrue(instances
        .getObjectList()
        .get(0)
        .getAssignmentList()
        .get(0)
        .getValue()
        .deepEquals(
            ODLMill.dateValueBuilder()
                .setDate(ODLMill.dateBuilder().setDay("01").setMonth("01").setYear("1999").build()).build()));
    
    assertEquals(instances.getObjectList().get(0).getAssignmentList().get(1).getName(), "name");
    assertTrue(instances
        .getObjectList()
        .get(0)
        .getAssignmentList()
        .get(1)
        .getValue()
        .deepEquals(
            ODLMill.stringValueBuilder()
                .setSTRING("alice").build()));
    
    assertEquals(instances.getObjectList().get(0).getAssignmentList().get(2).getName(), "id");
    assertTrue(instances
        .getObjectList()
        .get(0)
        .getAssignmentList()
        .get(2)
        .getValue()
        .deepEquals(
            ODLMill.intValueBuilder()
                .setINT("1").build()));
    
    assertEquals(instances.getObjectList().get(0).getAssignmentList().get(3).getName(), "car");
    assertTrue(instances
        .getObjectList()
        .get(0)
        .getAssignmentList()
        .get(3)
        .getValue()
        .deepEquals(
            ODLMill.referenceValueBuilder()
                .setName("car").build()));
    
    assertEquals(instances.getObjectList().get(1).getName(), "car");
    assertTrue(instances.getObjectList().get(1).getType().deepEquals(
        ODLMill.qualifiedNameBuilder().setNameList(Lists.newArrayList("lwc", "edl", "Car")).build()));
  }
  
}
