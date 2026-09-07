/* (c) https://github.com/MontiCore/monticore */

package mc.examples.lwc;

import com.google.common.collect.Lists;
import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.examples.lwc.edl.edl.EDLMill;
import mc.examples.lwc.edl.edl._ast.ASTEDLCompilationUnit;
import mc.examples.lwc.edl.edl._ast.ASTEntity;
import mc.examples.lwc.edl.edl._parser.EDLParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(EDLMill.class)
public class TestEDL {

  @Test
  public void testParser() throws IOException {
    EDLParser parser = EDLMill.parser();
    Optional<ASTEDLCompilationUnit> ast = parser
        .parseEDLCompilationUnit("src/test/resources/examples/lwc/edl/Car.edl");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertNotNull(ast.get().getEntity());
    
    ASTEntity entity = ast.get().getEntity();
    
    assertEquals("Car", entity.getName());
    assertEquals(7, entity.getPropertyList().size());
    
    assertEquals("brand", entity.getPropertyList().get(0).getName());
    assertTrue(entity.getPropertyList().get(0).getType()
        .deepEquals(EDLMill.stringLiteralBuilder().build()));
    
    assertEquals("model", entity.getPropertyList().get(1).getName());
    assertTrue(entity.getPropertyList().get(1).getType()
        .deepEquals(EDLMill.stringLiteralBuilder().build()));
    
    assertEquals("price", entity.getPropertyList().get(2).getName());
    assertTrue(entity.getPropertyList().get(2).getType()
        .deepEquals(EDLMill.intLiteralBuilder().build()));
    
    assertEquals("age", entity.getPropertyList().get(3).getName());
    assertTrue(entity.getPropertyList().get(3).getType()
        .deepEquals(EDLMill.intLiteralBuilder().build()));
    
    assertEquals("doors", entity.getPropertyList().get(4).getName());
    assertTrue(entity.getPropertyList().get(4).getType()
        .deepEquals(EDLMill.intLiteralBuilder().build()));
    
    assertEquals("myself", entity.getPropertyList().get(5).getName());
    assertTrue(entity
        .getPropertyList()
        .get(5)
        .getType()
        .deepEquals(
            EDLMill.referenceTypeBuilder()
                .setQualifiedName(
                    EDLMill.qualifiedNameBuilder().setNamesList(Lists.newArrayList("Car")).build()).build()));
    
    assertEquals("owner", entity.getPropertyList().get(6).getName());
    assertTrue(entity
        .getPropertyList()
        .get(6)
        .getType()
        .deepEquals(
            EDLMill.referenceTypeBuilder()
                .setQualifiedName(
                    EDLMill.qualifiedNameBuilder().setNamesList(Lists.newArrayList("lwc", "edl", "Person"))
                        .build()).build()));
  }
  
}
