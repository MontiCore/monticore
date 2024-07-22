/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.mc2cd.transl;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.codegen.mc2cd.TranslationTestCase;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static de.monticore.codegen.cd2java.DecoratorTestUtil.getInterfaceBy;

public class ExternalInterfaceTranslationTest extends TranslationTestCase {

  private ASTCDCompilationUnit externalInterface;

  @BeforeEach
  public void setUp() {
    externalInterface = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/ExternalInterface.mc4")).get();
  }

  @Test
  public void testExternalInterface(){
    ASTCDInterface a = getInterfaceBy("ASTAExt", externalInterface);
    Assertions.assertTrue(a.getModifier().isPresentStereotype());
    Assertions.assertEquals(1, a.getModifier().getStereotype().sizeValues());
    Assertions.assertEquals("externalInterface", a.getModifier().getStereotype().getValues(0).getName());
    Assertions.assertFalse(a.getModifier().getStereotype().getValues(0).isPresentText());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
