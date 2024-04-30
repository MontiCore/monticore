/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.mc2cd.transl;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.codegen.mc2cd.TranslationTestCase;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.*;

public class ComponetTranslationTest extends TranslationTestCase {

  private ASTCDCompilationUnit componentCD;

  private ASTCDCompilationUnit nonComponentCD;

  @Before
  public void setUp() {
    componentCD = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/AbstractProd.mc4")).get();
    nonComponentCD = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/AstRuleInheritance.mc4")).get();
  }

  @Test
  public void testIsComponent() {
    assertTrue(componentCD.getCDDefinition().getModifier().isPresentStereotype());
    assertEquals(1, componentCD.getCDDefinition().getModifier().getStereotype().sizeValues());
    assertEquals("component",componentCD.getCDDefinition().getModifier().getStereotype().getValues(0).getName());
    assertFalse(componentCD.getCDDefinition().getModifier().getStereotype().getValues(0).isPresentText());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testIsNotComponent() {
    assertFalse(nonComponentCD.getCDDefinition().getModifier().isPresentStereotype());
  
    assertTrue(Log.getFindings().isEmpty());
  }

}
