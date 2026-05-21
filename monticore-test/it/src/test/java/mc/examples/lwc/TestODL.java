/* (c) https://github.com/MontiCore/monticore */

package mc.examples.lwc;

import java.io.IOException;
import java.util.Optional;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.examples.lwc.odl.odl.ODLMill;
import org.junit.jupiter.api.BeforeEach;

import com.google.common.collect.Lists;

import mc.GeneratorIntegrationsTest;
import mc.examples.lwc.odl.odl._ast.ASTInstances;
import mc.examples.lwc.odl.odl._ast.ASTODLCompilationUnit;
import mc.examples.lwc.odl.odl._parser.ODLParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestODL extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testParser() throws IOException {
    ODLParser parser = new ODLParser();
    Optional<ASTODLCompilationUnit> ast = parser
        .parseODLCompilationUnit("src/test/resources/examples/lwc/odl/MyWorld.odl");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    
    ASTInstances instances = ast.get().getInstances();
    assertNotNull(instances);
    
    assertEquals("MyWorld", instances.getName());
    assertEquals(2, instances.getObjectList().size());
    
    assertEquals("person", instances.getObjectList().get(0).getName());
    assertTrue(instances.getObjectList().get(0).getType().deepEquals(
        ODLMill.qualifiedNameBuilder().setNamesList(Lists.newArrayList("Person")).build()));
    
    assertEquals(4, instances.getObjectList().get(0).getAssignmentList().size());
    assertEquals("birthday", instances.getObjectList().get(0).getAssignmentList().get(0).getName());
    assertTrue(instances
        .getObjectList()
        .get(0)
        .getAssignmentList()
        .get(0)
        .getValue()
        .deepEquals(
            ODLMill.dateValueBuilder()
                .setDate(ODLMill.dateBuilder().setDay("01").setMonth("01").setYear("1999").build()).build()));
    
    assertEquals("name", instances.getObjectList().get(0).getAssignmentList().get(1).getName());
    assertTrue(instances
        .getObjectList()
        .get(0)
        .getAssignmentList()
        .get(1)
        .getValue()
        .deepEquals(
            ODLMill.stringValueBuilder()
                .setSTRING("alice").build()));
    
    assertEquals("id", instances.getObjectList().get(0).getAssignmentList().get(2).getName());
    assertTrue(instances
        .getObjectList()
        .get(0)
        .getAssignmentList()
        .get(2)
        .getValue()
        .deepEquals(
            ODLMill.intValueBuilder()
                .setINT("1").build()));
    
    assertEquals("car", instances.getObjectList().get(0).getAssignmentList().get(3).getName());
    assertTrue(instances
        .getObjectList()
        .get(0)
        .getAssignmentList()
        .get(3)
        .getValue()
        .deepEquals(
            ODLMill.referenceValueBuilder()
                .setName("car").build()));
    
    assertEquals("car", instances.getObjectList().get(1).getName());
    assertTrue(instances.getObjectList().get(1).getType().deepEquals(
        ODLMill.qualifiedNameBuilder().setNamesList(Lists.newArrayList("lwc", "edl", "Car")).build()));
    assertTrue(Log.getFindings().isEmpty());
  }
  
}
