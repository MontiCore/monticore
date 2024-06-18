/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.mc2cd.transl;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.codegen.mc2cd.TranslationTestCase;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;

import static de.monticore.codegen.cd2java.DecoratorTestUtil.getInterfaceBy;
import static org.junit.Assert.*;

public class ExternalInterfaceTranslationTest extends TranslationTestCase {

  private ASTCDCompilationUnit externalInterface;

  @Before
  public void setUp() {
    externalInterface = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/ExternalInterface.mc4")).get();
  }

  @Test
  public void testExternalInterface(){
    ASTCDInterface a = getInterfaceBy("ASTAExt", externalInterface);
    assertTrue(a.getModifier().isPresentStereotype());
    assertEquals(1, a.getModifier().getStereotype().sizeValues());
    assertEquals("externalInterface", a.getModifier().getStereotype().getValues(0).getName());
    assertFalse(a.getModifier().getStereotype().getValues(0).isPresentText());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
