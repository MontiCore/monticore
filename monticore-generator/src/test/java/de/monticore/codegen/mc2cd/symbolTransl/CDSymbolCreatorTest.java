package de.monticore.codegen.mc2cd.symbolTransl;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.codegen.cd2java.factories.CDModifier;
import de.monticore.codegen.mc2cd.TestHelper;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getInterfaceBy;
import static org.junit.Assert.*;

public class CDSymbolCreatorTest {

  private ASTCDCompilationUnit compilationUnit;

  @Before
  public void setUp() {
    compilationUnit = TestHelper.parseAndTransformForSymbol(Paths
        .get("src/test/resources/mc2cdtransformation/symbolTransl/SymbolRule.mc4")).get();
  }

  @Test
  public void testDefinitionName() {
    assertEquals("SymbolRule", compilationUnit.getCDDefinition().getName());
  }

  @Test
  public void testPackage() {
    assertEquals(2, compilationUnit.getPackageList().size());
    assertEquals("mc2cdtransformation", compilationUnit.getPackage(0));
    assertEquals("symbolTransl", compilationUnit.getPackage(1));
  }

  @Test
  public void testClassCount() {
    assertEquals(2, compilationUnit.getCDDefinition().sizeCDClasss());
  }

  @Test
  public void testClassSymbol() {
    ASTCDClass symbolClassSymbol = getClassBy("SymbolClassSymbol", compilationUnit);
    assertEquals(1, symbolClassSymbol.sizeCDAttributes());
    assertTrue(symbolClassSymbol.isEmptyInterfaces());
    assertTrue(symbolClassSymbol.isEmptyCDMethods());
    assertTrue(symbolClassSymbol.isEmptyCDConstructors());
    assertFalse(symbolClassSymbol.isPresentSuperclass());

    ASTCDAttribute cdAttribute = symbolClassSymbol.getCDAttribute(0);
    assertEquals("extraString", cdAttribute.getName());
    assertDeepEquals(String.class, cdAttribute.getMCType());
    assertTrue(cdAttribute.isPresentModifier());
    assertDeepEquals(CDModifier.PROTECTED, cdAttribute.getModifier());
  }

  @Test
  public void testAbstractClassSymbol() {
    ASTCDClass symbolClassSymbol = getClassBy("SymbolAbstractClassSymbol", compilationUnit);
    assertEquals(1, symbolClassSymbol.sizeCDAttributes());
    assertTrue(symbolClassSymbol.isEmptyInterfaces());
    assertTrue(symbolClassSymbol.isEmptyCDMethods());
    assertTrue(symbolClassSymbol.isEmptyCDConstructors());
    assertFalse(symbolClassSymbol.isPresentSuperclass());

    ASTCDAttribute cdAttribute = symbolClassSymbol.getCDAttribute(0);
    assertEquals("extraString", cdAttribute.getName());
    assertDeepEquals(String.class, cdAttribute.getMCType());
    assertTrue(cdAttribute.isPresentModifier());
    assertDeepEquals(CDModifier.PROTECTED, cdAttribute.getModifier());
  }

  @Test
  public void testInterfaceCount() {
    assertEquals(2, compilationUnit.getCDDefinition().sizeCDClasss());

  }

  @Test
  public void testInterfaceSymbol() {
    ASTCDInterface symbolClassSymbol = getInterfaceBy("SymbolInterfaceSymbol", compilationUnit);
    assertEquals(1, symbolClassSymbol.sizeCDAttributes());
    assertTrue(symbolClassSymbol.isEmptyInterfaces());
    assertTrue(symbolClassSymbol.isEmptyCDMethods());

    ASTCDAttribute cdAttribute = symbolClassSymbol.getCDAttribute(0);
    assertEquals("extraString", cdAttribute.getName());
    assertDeepEquals(String.class, cdAttribute.getMCType());
    assertTrue(cdAttribute.isPresentModifier());
    assertDeepEquals(CDModifier.PROTECTED, cdAttribute.getModifier());
  }

  @Test
  public void testExternalSymbol() {
    ASTCDInterface symbolClassSymbol = getInterfaceBy("SymbolExternalSymbol", compilationUnit);
    assertEquals(1, symbolClassSymbol.sizeCDAttributes());
    assertTrue(symbolClassSymbol.isEmptyInterfaces());
    assertTrue(symbolClassSymbol.isEmptyCDMethods());

    ASTCDAttribute cdAttribute = symbolClassSymbol.getCDAttribute(0);
    assertEquals("extraString", cdAttribute.getName());
    assertDeepEquals(String.class, cdAttribute.getMCType());
    assertTrue(cdAttribute.isPresentModifier());
    assertDeepEquals(CDModifier.PROTECTED, cdAttribute.getModifier());
  }
}


