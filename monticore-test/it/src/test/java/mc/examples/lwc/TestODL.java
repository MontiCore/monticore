/* (c) https://github.com/MontiCore/monticore */

package mc.examples.lwc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Optional;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.examples.lwc.odl.odl.ODLMill;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import com.google.common.collect.Lists;

import mc.GeneratorIntegrationsTest;
import mc.examples.lwc.odl.odl._ast.ASTInstances;
import mc.examples.lwc.odl.odl._ast.ASTODLCompilationUnit;
import mc.examples.lwc.odl.odl._parser.ODLParser;
import org.junit.jupiter.api.Test;

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
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    
    ASTInstances instances = ast.get().getInstances();
    Assertions.assertNotNull(instances);
    
    Assertions.assertEquals(instances.getName(), "MyWorld");
    Assertions.assertEquals(instances.getObjectList().size(), 2);
    
    Assertions.assertEquals(instances.getObjectList().get(0).getName(), "person");
    Assertions.assertTrue(instances.getObjectList().get(0).getType().deepEquals(
        ODLMill.qualifiedNameBuilder().setNamesList(Lists.newArrayList("Person")).build()));
    
    Assertions.assertEquals(instances.getObjectList().get(0).getAssignmentList().size(), 4);
    Assertions.assertEquals(instances.getObjectList().get(0).getAssignmentList().get(0).getName(), "birthday");
    Assertions.assertTrue(instances
        .getObjectList()
        .get(0)
        .getAssignmentList()
        .get(0)
        .getValue()
        .deepEquals(
            ODLMill.dateValueBuilder()
                .setDate(ODLMill.dateBuilder().setDay("01").setMonth("01").setYear("1999").build()).build()));
    
    Assertions.assertEquals(instances.getObjectList().get(0).getAssignmentList().get(1).getName(), "name");
    Assertions.assertTrue(instances
        .getObjectList()
        .get(0)
        .getAssignmentList()
        .get(1)
        .getValue()
        .deepEquals(
            ODLMill.stringValueBuilder()
                .setSTRING("alice").build()));
    
    Assertions.assertEquals(instances.getObjectList().get(0).getAssignmentList().get(2).getName(), "id");
    Assertions.assertTrue(instances
        .getObjectList()
        .get(0)
        .getAssignmentList()
        .get(2)
        .getValue()
        .deepEquals(
            ODLMill.intValueBuilder()
                .setINT("1").build()));
    
    Assertions.assertEquals(instances.getObjectList().get(0).getAssignmentList().get(3).getName(), "car");
    Assertions.assertTrue(instances
        .getObjectList()
        .get(0)
        .getAssignmentList()
        .get(3)
        .getValue()
        .deepEquals(
            ODLMill.referenceValueBuilder()
                .setName("car").build()));
    
    Assertions.assertEquals(instances.getObjectList().get(1).getName(), "car");
    Assertions.assertTrue(instances.getObjectList().get(1).getType().deepEquals(
        ODLMill.qualifiedNameBuilder().setNamesList(Lists.newArrayList("lwc", "edl", "Car")).build()));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
}
