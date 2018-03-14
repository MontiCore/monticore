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
import mc.examples.lwc.edl.edl._ast.ASTEDLCompilationUnit;
import mc.examples.lwc.edl.edl._ast.ASTEntity;
import mc.examples.lwc.edl.edl._ast.EDLMill;
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
    assertEquals(entity.getPropertyList().size(), 7);
    
    assertEquals(entity.getPropertyList().get(0).getName(), "brand");
    assertTrue(entity.getPropertyList().get(0).getType()
        .deepEquals(EDLMill.stringLiteralBuilder().build()));
    
    assertEquals(entity.getPropertyList().get(1).getName(), "model");
    assertTrue(entity.getPropertyList().get(1).getType()
        .deepEquals(EDLMill.stringLiteralBuilder().build()));
    
    assertEquals(entity.getPropertyList().get(2).getName(), "price");
    assertTrue(entity.getPropertyList().get(2).getType()
        .deepEquals(EDLMill.intLiteralBuilder().build()));
    
    assertEquals(entity.getPropertyList().get(3).getName(), "age");
    assertTrue(entity.getPropertyList().get(3).getType()
        .deepEquals(EDLMill.intLiteralBuilder().build()));
    
    assertEquals(entity.getPropertyList().get(4).getName(), "doors");
    assertTrue(entity.getPropertyList().get(4).getType()
        .deepEquals(EDLMill.intLiteralBuilder().build()));
    
    assertEquals(entity.getPropertyList().get(5).getName(), "myself");
    assertTrue(entity
        .getPropertyList()
        .get(5)
        .getType()
        .deepEquals(
            EDLMill.referenceTypeBuilder()
                .setQualifiedName(
                    EDLMill.qualifiedNameBuilder().setNameList(Lists.newArrayList("Car")).build()).build()));
    
    assertEquals(entity.getPropertyList().get(6).getName(), "owner");
    assertTrue(entity
        .getPropertyList()
        .get(6)
        .getType()
        .deepEquals(
            EDLMill.referenceTypeBuilder()
                .setQualifiedName(
                    EDLMill.qualifiedNameBuilder().setNameList(Lists.newArrayList("lwc", "edl", "Person"))
                        .build()).build()));
    
  }
  
}
