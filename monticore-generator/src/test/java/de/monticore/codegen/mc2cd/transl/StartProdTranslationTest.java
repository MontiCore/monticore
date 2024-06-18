/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.mc2cd.transl;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.codegen.mc2cd.TranslationTestCase;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;

import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getInterfaceBy;
import static org.junit.Assert.*;

public class StartProdTranslationTest extends TranslationTestCase {

  private ASTCDCompilationUnit globalStartProd;

  private ASTCDCompilationUnit classStartProd;

  private ASTCDCompilationUnit interfaceStartProd;

  @Before
  public void setUpStartProdTranslationTest() {
    globalStartProd = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/StartProd.mc4")).get();
    classStartProd = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/Supergrammar.mc4")).get();
    interfaceStartProd = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/InterfaceProd.mc4")).get();
  }

  @Test
  public void testGlobalStartProd() {
    assertTrue(globalStartProd.getCDDefinition().getModifier().isPresentStereotype());
    assertEquals(1, globalStartProd.getCDDefinition().getModifier().getStereotype().sizeValues());
    assertEquals("startProd", globalStartProd.getCDDefinition().getModifier().getStereotype().getValues(0).getName());
    assertFalse(globalStartProd.getCDDefinition().getModifier().getStereotype().getValues(0).getValue().isEmpty());
    assertEquals("mc2cdtransformation.Supergrammar.X", globalStartProd.getCDDefinition().getModifier().getStereotype().getValues(0).getValue());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testClassStartProd() {
    ASTCDClass xClass = getClassBy("ASTX", classStartProd);
    assertTrue(xClass.getModifier().isPresentStereotype());
    assertEquals(1, xClass.getModifier().getStereotype().sizeValues());
    assertEquals("startProd", xClass.getModifier().getStereotype().getValues(0).getName());
    assertTrue(xClass.getModifier().getStereotype().getValues(0).getValue().isEmpty());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInterfaceStartProd() {
    ASTCDInterface aInterface = getInterfaceBy("ASTA", interfaceStartProd);
    assertTrue(aInterface.getModifier().isPresentStereotype());
    assertEquals(1, aInterface.getModifier().getStereotype().sizeValues());
    assertEquals("startProd", aInterface.getModifier().getStereotype().getValues(0).getName());
    assertTrue(aInterface.getModifier().getStereotype().getValues(0).getValue().isEmpty());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
