/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.mc2cd.transl;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.codegen.mc2cd.TranslationTestCase;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

public class ComponetTranslationTest extends TranslationTestCase {

  private ASTCDCompilationUnit componentCD;

  private ASTCDCompilationUnit nonComponentCD;

  @BeforeEach
  public void setUp() {
    componentCD = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/AbstractProd.mc4")).get();
    nonComponentCD = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/AstRuleInheritance.mc4")).get();
  }

  @Test
  public void testIsComponent() {
    Assertions.assertTrue(componentCD.getCDDefinition().getModifier().isPresentStereotype());
    Assertions.assertEquals(1, componentCD.getCDDefinition().getModifier().getStereotype().sizeValues());
    Assertions.assertEquals("component", componentCD.getCDDefinition().getModifier().getStereotype().getValues(0).getName());
    Assertions.assertFalse(componentCD.getCDDefinition().getModifier().getStereotype().getValues(0).isPresentText());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testIsNotComponent() {
    Assertions.assertFalse(nonComponentCD.getCDDefinition().getModifier().isPresentStereotype());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}
