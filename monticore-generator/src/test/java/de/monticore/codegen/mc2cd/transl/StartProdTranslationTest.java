/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.mc2cd.transl;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.codegen.mc2cd.TranslationTestCase;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getInterfaceBy;

public class StartProdTranslationTest extends TranslationTestCase {

  private ASTCDCompilationUnit globalStartProd;

  private ASTCDCompilationUnit classStartProd;

  private ASTCDCompilationUnit interfaceStartProd;

  @BeforeEach
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
    Assertions.assertTrue(globalStartProd.getCDDefinition().getModifier().isPresentStereotype());
    Assertions.assertEquals(1, globalStartProd.getCDDefinition().getModifier().getStereotype().sizeValues());
    Assertions.assertEquals("startProd", globalStartProd.getCDDefinition().getModifier().getStereotype().getValues(0).getName());
    Assertions.assertFalse(globalStartProd.getCDDefinition().getModifier().getStereotype().getValues(0).getValue().isEmpty());
    Assertions.assertEquals("mc2cdtransformation.Supergrammar.X", globalStartProd.getCDDefinition().getModifier().getStereotype().getValues(0).getValue());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testClassStartProd() {
    ASTCDClass xClass = getClassBy("ASTX", classStartProd);
    Assertions.assertTrue(xClass.getModifier().isPresentStereotype());
    Assertions.assertEquals(1, xClass.getModifier().getStereotype().sizeValues());
    Assertions.assertEquals("startProd", xClass.getModifier().getStereotype().getValues(0).getName());
    Assertions.assertTrue(xClass.getModifier().getStereotype().getValues(0).getValue().isEmpty());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInterfaceStartProd() {
    ASTCDInterface aInterface = getInterfaceBy("ASTA", interfaceStartProd);
    Assertions.assertTrue(aInterface.getModifier().isPresentStereotype());
    Assertions.assertEquals(1, aInterface.getModifier().getStereotype().sizeValues());
    Assertions.assertEquals("startProd", aInterface.getModifier().getStereotype().getValues(0).getName());
    Assertions.assertTrue(aInterface.getModifier().getStereotype().getValues(0).getValue().isEmpty());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
