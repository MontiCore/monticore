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
import mc.examples.lwc.edl.edl.EDLMill;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import com.google.common.collect.Lists;

import mc.GeneratorIntegrationsTest;
import mc.examples.lwc.edl.edl._ast.ASTEDLCompilationUnit;
import mc.examples.lwc.edl.edl._ast.ASTEntity;
import mc.examples.lwc.edl.edl._parser.EDLParser;
import org.junit.jupiter.api.Test;

public class TestEDL extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testParser() throws IOException {
    EDLParser parser = new EDLParser();
    Optional<ASTEDLCompilationUnit> ast = parser
        .parseEDLCompilationUnit("src/test/resources/examples/lwc/edl/Car.edl");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertNotNull(ast.get().getEntity());
    
    ASTEntity entity = ast.get().getEntity();
    
    Assertions.assertEquals(entity.getName(), "Car");
    Assertions.assertEquals(entity.getPropertyList().size(), 7);
    
    Assertions.assertEquals(entity.getPropertyList().get(0).getName(), "brand");
    Assertions.assertTrue(entity.getPropertyList().get(0).getType()
        .deepEquals(EDLMill.stringLiteralBuilder().build()));
    
    Assertions.assertEquals(entity.getPropertyList().get(1).getName(), "model");
    Assertions.assertTrue(entity.getPropertyList().get(1).getType()
        .deepEquals(EDLMill.stringLiteralBuilder().build()));
    
    Assertions.assertEquals(entity.getPropertyList().get(2).getName(), "price");
    Assertions.assertTrue(entity.getPropertyList().get(2).getType()
        .deepEquals(EDLMill.intLiteralBuilder().build()));
    
    Assertions.assertEquals(entity.getPropertyList().get(3).getName(), "age");
    Assertions.assertTrue(entity.getPropertyList().get(3).getType()
        .deepEquals(EDLMill.intLiteralBuilder().build()));
    
    Assertions.assertEquals(entity.getPropertyList().get(4).getName(), "doors");
    Assertions.assertTrue(entity.getPropertyList().get(4).getType()
        .deepEquals(EDLMill.intLiteralBuilder().build()));
    
    Assertions.assertEquals(entity.getPropertyList().get(5).getName(), "myself");
    Assertions.assertTrue(entity
        .getPropertyList()
        .get(5)
        .getType()
        .deepEquals(
            EDLMill.referenceTypeBuilder()
                .setQualifiedName(
                    EDLMill.qualifiedNameBuilder().setNamesList(Lists.newArrayList("Car")).build()).build()));
    
    Assertions.assertEquals(entity.getPropertyList().get(6).getName(), "owner");
    Assertions.assertTrue(entity
        .getPropertyList()
        .get(6)
        .getType()
        .deepEquals(
            EDLMill.referenceTypeBuilder()
                .setQualifiedName(
                    EDLMill.qualifiedNameBuilder().setNamesList(Lists.newArrayList("lwc", "edl", "Person"))
                        .build()).build()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
}
