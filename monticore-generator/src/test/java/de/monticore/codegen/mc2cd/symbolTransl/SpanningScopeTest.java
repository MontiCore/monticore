package de.monticore.codegen.mc2cd.symbolTransl;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDStereoValue;
import de.monticore.codegen.mc2cd.TestHelper;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;

import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static org.junit.Assert.*;

public class SpanningScopeTest {

  /***
   * tests if in symbol cd the scope spanning symbols have the stereotype scope
   */

  private ASTCDCompilationUnit compilationUnit;

  @Before
  public void setUp() {
    compilationUnit = TestHelper.parseAndTransformForSymbol(Paths
        .get("src/test/resources/mc2cdtransformation/symbolTransl/ScopeSpanning.mc4")).get();
  }

  @Test
  public void testDefinitionName() {
    assertEquals("ScopeSpanning", compilationUnit.getCDDefinition().getName());
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
  public void testScopeSpanningSymbol() {
    ASTCDClass symbolClassSymbol = getClassBy("ScopeSpanningSymbol", compilationUnit);
    assertTrue(symbolClassSymbol.isEmptyInterfaces());
    assertTrue(symbolClassSymbol.isEmptyCDMethods());
    assertTrue(symbolClassSymbol.isEmptyCDConstructors());
    assertFalse(symbolClassSymbol.isPresentSuperclass());
    assertTrue(symbolClassSymbol.isEmptyCDAttributes());

    assertTrue(symbolClassSymbol.isPresentModifier());

    assertTrue(symbolClassSymbol.getModifier().isPresentStereotype());
    assertFalse(symbolClassSymbol.getModifier().getStereotype().isEmptyValues());
    assertEquals(2, symbolClassSymbol.getModifier().getStereotype().sizeValues());
    ASTCDStereoValue symbolStereotype = symbolClassSymbol.getModifier().getStereotype().getValue(0);
    assertEquals("symbol", symbolStereotype.getName());
    ASTCDStereoValue scopeStereotype = symbolClassSymbol.getModifier().getStereotype().getValue(1);
    assertEquals("scope", scopeStereotype.getName());
  }

  @Test
  public void testOnlySymbol() {
    ASTCDClass symbolClassSymbol = getClassBy("OnlySymbolSymbol", compilationUnit);
    assertTrue(symbolClassSymbol.isEmptyInterfaces());
    assertTrue(symbolClassSymbol.isEmptyCDMethods());
    assertTrue(symbolClassSymbol.isEmptyCDConstructors());
    assertFalse(symbolClassSymbol.isPresentSuperclass());
    assertTrue(symbolClassSymbol.isEmptyCDAttributes());

    assertTrue(symbolClassSymbol.isPresentModifier());

    assertTrue(symbolClassSymbol.getModifier().isPresentStereotype());
    assertFalse(symbolClassSymbol.getModifier().getStereotype().isEmptyValues());
    assertEquals(2, symbolClassSymbol.getModifier().getStereotype().sizeValues());
    ASTCDStereoValue symbolStereotype = symbolClassSymbol.getModifier().getStereotype().getValue(0);
    assertEquals("symbol", symbolStereotype.getName());
    ASTCDStereoValue scopeStereotype = symbolClassSymbol.getModifier().getStereotype().getValue(1);
    assertEquals("startProd", scopeStereotype.getName());
  }
}
