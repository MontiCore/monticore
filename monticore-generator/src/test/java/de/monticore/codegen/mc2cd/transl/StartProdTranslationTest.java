package de.monticore.codegen.mc2cd.transl;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.codegen.mc2cd.TestHelper;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;

import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getInterfaceBy;
import static org.junit.Assert.*;

public class StartProdTranslationTest {

  private ASTCDCompilationUnit globalStartProd;

  private ASTCDCompilationUnit classStartProd;

  private ASTCDCompilationUnit interfaceStartProd;


  @Before
  public void setUp() {
    globalStartProd = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/StartProd.mc4")).get();
    classStartProd = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/Supergrammar.mc4")).get();
    interfaceStartProd = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/InterfaceProd.mc4")).get();
  }

  @Test
  public void testGlobalStartProd() {
    assertTrue(globalStartProd.getCDDefinition().isPresentModifier());
    assertTrue(globalStartProd.getCDDefinition().getModifier().isPresentStereotype());
    assertEquals(1, globalStartProd.getCDDefinition().getModifier().getStereotype().sizeValues());
    assertEquals("startProd", globalStartProd.getCDDefinition().getModifier().getStereotype().getValue(0).getName());
    assertTrue(globalStartProd.getCDDefinition().getModifier().getStereotype().getValue(0).isPresentValue());
    assertEquals("mc2cdtransformation.Supergrammar.X", globalStartProd.getCDDefinition().getModifier().getStereotype().getValue(0).getValue());
  }

  @Test
  public void testClassStartProd() {
    assertFalse(classStartProd.getCDDefinition().isPresentModifier());

    ASTCDClass xClass = getClassBy("ASTX", classStartProd);
    assertTrue(xClass.isPresentModifier());
    assertTrue(xClass.getModifier().isPresentStereotype());
    assertEquals(1, xClass.getModifier().getStereotype().sizeValues());
    assertEquals("startProd", xClass.getModifier().getStereotype().getValue(0).getName());
    assertFalse(xClass.getModifier().getStereotype().getValue(0).isPresentValue());
  }

  @Test
  public void testInterfaceStartProd() {
    assertFalse(interfaceStartProd.getCDDefinition().isPresentModifier());

    ASTCDInterface aInterface = getInterfaceBy("ASTA", interfaceStartProd);
    assertTrue(aInterface.isPresentModifier());
    assertTrue(aInterface.getModifier().isPresentStereotype());
    assertEquals(1, aInterface.getModifier().getStereotype().sizeValues());
    assertEquals("startProd", aInterface.getModifier().getStereotype().getValue(0).getName());
    assertFalse(aInterface.getModifier().getStereotype().getValue(0).isPresentValue());
  }
}
