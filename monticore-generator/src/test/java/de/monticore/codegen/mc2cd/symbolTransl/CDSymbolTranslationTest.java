package de.monticore.codegen.mc2cd.symbolTransl;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.factories.CDModifier;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static org.junit.Assert.*;

public class CDSymbolTranslationTest {

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
    assertEquals(4, compilationUnit.getCDDefinition().sizeCDClasss());
  }

  @Test
  public void testClassSymbol() {
    ASTCDClass symbolClassSymbol = getClassBy("SymbolClass", compilationUnit);
    assertEquals(1, symbolClassSymbol.sizeCDAttributes());
    assertEquals(1, symbolClassSymbol.sizeInterfaces());
    assertEquals(1, symbolClassSymbol.sizeCDMethods());
    assertTrue(symbolClassSymbol.isEmptyCDConstructors());
    assertTrue(symbolClassSymbol.isPresentSuperclass());

    ASTCDAttribute cdAttribute = symbolClassSymbol.getCDAttribute(0);
    assertEquals("extraString", cdAttribute.getName());
    assertDeepEquals(String.class, cdAttribute.getMCType());
    assertTrue(cdAttribute.isPresentModifier());
    assertDeepEquals(CDModifier.PROTECTED, cdAttribute.getModifier());

    ASTCDMethod cdMethod = symbolClassSymbol.getCDMethod(0);
    assertEquals("toString", cdMethod.getName());
    assertTrue(cdMethod.getMCReturnType().isPresentMCType());
    assertDeepEquals(String.class, cdMethod.getMCReturnType().getMCType());
    assertTrue(cdMethod.getModifier().isPublic());
    assertTrue(cdMethod.getModifier().isPresentStereotype());
    assertEquals(1, cdMethod.getModifier().getStereotype().sizeValues());
    assertEquals("methodBody", cdMethod.getModifier().getStereotype().getValue(0).getName());

    ASTMCObjectType cdInterface = symbolClassSymbol.getInterface(0);
    assertDeepEquals("de.monticore.symboltable.ISymbol", cdInterface);

    ASTMCObjectType superclass = symbolClassSymbol.getSuperclass();
    assertDeepEquals("de.monticore.symboltable.Symbol", superclass);
  }

  @Test
  public void testAbstractClassSymbol() {
    ASTCDClass symbolClassSymbol = getClassBy("SymbolAbstractClass", compilationUnit);
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
    assertTrue(compilationUnit.getCDDefinition().isEmptyCDInterfaces());

  }

  @Test
  public void testInterfaceSymbol() {
    ASTCDClass symbolClassSymbol = getClassBy("SymbolInterface", compilationUnit);
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
    ASTCDClass symbolClassSymbol = getClassBy("SymbolExternal", compilationUnit);
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


