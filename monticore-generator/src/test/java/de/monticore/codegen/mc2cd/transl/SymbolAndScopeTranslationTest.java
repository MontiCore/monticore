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

public class SymbolAndScopeTranslationTest {

  private ASTCDCompilationUnit symbolCD;

  @Before
  public void setUp() {
    symbolCD = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/SymbolAndScopeTranslation.mc4")).get();
  }

  /**
   * symbol test
   */
  @Test
  public void testSimpleSymbolInterface() {
    ASTCDInterface astType = getInterfaceBy("ASTType", symbolCD);
    assertTrue(astType.isPresentModifier());
    assertTrue(astType.getModifier().isPresentStereotype());
    // only 2 because other one is the start prod stereotype
    assertEquals(2, astType.getModifier().getStereotype().getValueList().size());
    assertEquals("symbol", astType.getModifier().getStereotype().getValue(0).getName());
    assertFalse(astType.getModifier().getStereotype().getValue(0).isPresentValue());
    assertEquals("startProd", astType.getModifier().getStereotype().getValue(1).getName());
    assertFalse(astType.getModifier().getStereotype().getValue(1).isPresentValue());
  }

  @Test
  public void testInheritedSymbolInterface_OneLevel() {
    ASTCDInterface astType = getInterfaceBy("ASTCDType", symbolCD);
    assertTrue(astType.isPresentModifier());
    assertTrue(astType.getModifier().isPresentStereotype());

    assertEquals(1, astType.getModifier().getStereotype().getValueList().size());
    assertEquals("inheritedSymbol", astType.getModifier().getStereotype().getValue(0).getName());
    assertTrue(astType.getModifier().getStereotype().getValue(0).isPresentValue());
    assertEquals("mc2cdtransformation.symbolandscopetranslation._symboltable.TypeSymbol",
        astType.getModifier().getStereotype().getValue(0).getValue());
  }

  @Test
  public void testInheritedSymbolInterface_TwoLevels() {
    ASTCDClass astType = getClassBy("ASTCDClass", symbolCD);
    assertTrue(astType.isPresentModifier());
    assertTrue(astType.getModifier().isPresentStereotype());

    assertEquals(1, astType.getModifier().getStereotype().getValueList().size());
    assertEquals("inheritedSymbol", astType.getModifier().getStereotype().getValue(0).getName());
    assertTrue(astType.getModifier().getStereotype().getValue(0).isPresentValue());
    assertEquals("mc2cdtransformation.symbolandscopetranslation._symboltable.TypeSymbol",
        astType.getModifier().getStereotype().getValue(0).getValue());
  }

  @Test
  public void testSimpleSymbolClass() {
    ASTCDClass astType = getClassBy("ASTSimpleSymbolClass", symbolCD);
    assertTrue(astType.isPresentModifier());
    assertTrue(astType.getModifier().isPresentStereotype());
    assertEquals(1, astType.getModifier().getStereotype().getValueList().size());
    assertEquals("symbol", astType.getModifier().getStereotype().getValue(0).getName());
    assertFalse(astType.getModifier().getStereotype().getValue(0).isPresentValue());
  }

  @Test
  public void testSymbolClassOverwritten() {
    ASTCDClass astType = getClassBy("ASTSymbolClass", symbolCD);
    assertTrue(astType.isPresentModifier());
    assertTrue(astType.getModifier().isPresentStereotype());
    assertEquals(2, astType.getModifier().getStereotype().getValueList().size());
    assertEquals("inheritedSymbol", astType.getModifier().getStereotype().getValue(0).getName());
    assertTrue(astType.getModifier().getStereotype().getValue(0).isPresentValue());
    assertEquals("mc2cdtransformation.symboltransl.symbolrule._symboltable.SymbolClassSymbol",
        astType.getModifier().getStereotype().getValue(0).getValue());

    assertEquals("inheritedScope", astType.getModifier().getStereotype().getValue(1).getName());
    assertFalse(astType.getModifier().getStereotype().getValue(1).isPresentValue());
  }

  @Test
  public void testSymbolClassExtents() {
    ASTCDClass astType = getClassBy("ASTExtentsSymbolClass", symbolCD);
    assertTrue(astType.isPresentModifier());
    assertTrue(astType.getModifier().isPresentStereotype());
    assertEquals(2, astType.getModifier().getStereotype().getValueList().size());
    assertEquals("inheritedSymbol", astType.getModifier().getStereotype().getValue(0).getName());
    assertTrue(astType.getModifier().getStereotype().getValue(0).isPresentValue());
    assertEquals("mc2cdtransformation.symbolandscopetranslation._symboltable.FooSymbol",
        astType.getModifier().getStereotype().getValue(0).getValue());

    assertEquals("inheritedScope", astType.getModifier().getStereotype().getValue(1).getName());
    assertFalse(astType.getModifier().getStereotype().getValue(1).isPresentValue());
  }

  @Test
  public void testSymbolInterfaceOverwritten() {
    ASTCDClass astType = getClassBy("ASTSymbolInterface", symbolCD);
    assertTrue(astType.isPresentModifier());
    assertTrue(astType.getModifier().isPresentStereotype());
    assertEquals(1, astType.getModifier().getStereotype().getValueList().size());
    assertEquals("inheritedSymbol", astType.getModifier().getStereotype().getValue(0).getName());
    assertTrue(astType.getModifier().getStereotype().getValue(0).isPresentValue());
    assertEquals("mc2cdtransformation.symboltransl.symbolrule._symboltable.SymbolInterfaceSymbol",
        astType.getModifier().getStereotype().getValue(0).getValue());
  }

  @Test
  public void testSymbolInterfaceImplements() {
    ASTCDClass astType = getClassBy("ASTImplementsSymbolInterface", symbolCD);
    assertTrue(astType.isPresentModifier());
    assertTrue(astType.getModifier().isPresentStereotype());
    assertEquals(2, astType.getModifier().getStereotype().getValueList().size());
    assertEquals("inheritedSymbol", astType.getModifier().getStereotype().getValue(0).getName());
    assertTrue(astType.getModifier().getStereotype().getValue(0).isPresentValue());
    assertEquals("mc2cdtransformation.symbolandscopetranslation._symboltable.InterfaceFooSymbol",
        astType.getModifier().getStereotype().getValue(0).getValue());

    assertEquals("inheritedScope", astType.getModifier().getStereotype().getValue(1).getName());
    assertFalse(astType.getModifier().getStereotype().getValue(1).isPresentValue());

  }

  @Test
  public void testSymbolAbstractClassOverwritten() {
    ASTCDClass astType = getClassBy("ASTSymbolAbstractClass", symbolCD);
    assertTrue(astType.isPresentModifier());
    assertTrue(astType.getModifier().isPresentStereotype());
    assertEquals(1, astType.getModifier().getStereotype().getValueList().size());
    assertEquals("inheritedSymbol", astType.getModifier().getStereotype().getValue(0).getName());
    assertTrue(astType.getModifier().getStereotype().getValue(0).isPresentValue());
    assertEquals("mc2cdtransformation.symboltransl.symbolrule._symboltable.SymbolAbstractClassSymbol",
        astType.getModifier().getStereotype().getValue(0).getValue());
  }

  @Test
  public void testSymbolAbstractClassExtents() {
    ASTCDClass astType = getClassBy("ASTExtentsSymbolAbstractClass", symbolCD);
    assertTrue(astType.isPresentModifier());
    assertTrue(astType.getModifier().isPresentStereotype());
    assertEquals(2, astType.getModifier().getStereotype().getValueList().size());
    assertEquals("inheritedSymbol", astType.getModifier().getStereotype().getValue(0).getName());
    assertTrue(astType.getModifier().getStereotype().getValue(0).isPresentValue());
    assertEquals("mc2cdtransformation.symbolandscopetranslation._symboltable.AbstractFooSymbol",
        astType.getModifier().getStereotype().getValue(0).getValue());

    assertEquals("inheritedScope", astType.getModifier().getStereotype().getValue(1).getName());
    assertFalse(astType.getModifier().getStereotype().getValue(1).isPresentValue());
  }

  /**
   * scope test
   */
  @Test
  public void testSimpleScopeInterface() {
    ASTCDInterface astType = getInterfaceBy("ASTTypeScope", symbolCD);
    assertTrue(astType.isPresentModifier());
    assertTrue(astType.getModifier().isPresentStereotype());
    assertEquals(1, astType.getModifier().getStereotype().getValueList().size());
    assertEquals("scope", astType.getModifier().getStereotype().getValue(0).getName());
    assertFalse(astType.getModifier().getStereotype().getValue(0).isPresentValue());
  }

  @Test
  public void testInheritedScopeInterface_OneLevel() {
    ASTCDInterface astType = getInterfaceBy("ASTCDTypeScope", symbolCD);
    assertTrue(astType.isPresentModifier());
    assertTrue(astType.getModifier().isPresentStereotype());

    assertEquals(1, astType.getModifier().getStereotype().getValueList().size());
    assertEquals("inheritedScope", astType.getModifier().getStereotype().getValue(0).getName());
    assertFalse(astType.getModifier().getStereotype().getValue(0).isPresentValue());
  }

  @Test
  public void testInheritedScopeInterface_TwoLevels() {
    ASTCDClass astType = getClassBy("ASTCDClassScope", symbolCD);
    assertTrue(astType.isPresentModifier());
    assertTrue(astType.getModifier().isPresentStereotype());

    assertEquals(1, astType.getModifier().getStereotype().getValueList().size());
    assertEquals("inheritedScope", astType.getModifier().getStereotype().getValue(0).getName());
    assertFalse(astType.getModifier().getStereotype().getValue(0).isPresentValue());
  }

  @Test
  public void testSimpleScopeClass() {
    ASTCDClass astType = getClassBy("ASTSimpleScopeClass", symbolCD);
    assertTrue(astType.isPresentModifier());
    assertTrue(astType.getModifier().isPresentStereotype());
    assertEquals(1, astType.getModifier().getStereotype().getValueList().size());
    assertEquals("scope", astType.getModifier().getStereotype().getValue(0).getName());
    assertFalse(astType.getModifier().getStereotype().getValue(0).isPresentValue());
  }

  @Test
  public void testNoShadowingScopeClass() {
    ASTCDClass astType = getClassBy("ASTScopeNoShadowing", symbolCD);
    assertTrue(astType.isPresentModifier());
    assertTrue(astType.getModifier().isPresentStereotype());
    assertEquals(2, astType.getModifier().getStereotype().getValueList().size());
    assertEquals("scope", astType.getModifier().getStereotype().getValue(0).getName());
    assertFalse(astType.getModifier().getStereotype().getValue(0).isPresentValue());
    assertEquals("no_shadowing", astType.getModifier().getStereotype().getValue(1).getName());
    assertFalse(astType.getModifier().getStereotype().getValue(1).isPresentValue());
  }

  @Test
  public void testNoExportingScopeClass() {
    ASTCDClass astType = getClassBy("ASTScopeNoExporting", symbolCD);
    assertTrue(astType.isPresentModifier());
    assertTrue(astType.getModifier().isPresentStereotype());
    assertEquals(2, astType.getModifier().getStereotype().getValueList().size());
    assertEquals("scope", astType.getModifier().getStereotype().getValue(0).getName());
    assertFalse(astType.getModifier().getStereotype().getValue(0).isPresentValue());
    assertEquals("no_exporting", astType.getModifier().getStereotype().getValue(1).getName());
    assertFalse(astType.getModifier().getStereotype().getValue(1).isPresentValue());
  }

  @Test
  public void testOrderedScopeClass() {
    ASTCDClass astType = getClassBy("ASTScopeOrdered", symbolCD);
    assertTrue(astType.isPresentModifier());
    assertTrue(astType.getModifier().isPresentStereotype());
    assertEquals(2, astType.getModifier().getStereotype().getValueList().size());
    assertEquals("scope", astType.getModifier().getStereotype().getValue(0).getName());
    assertFalse(astType.getModifier().getStereotype().getValue(0).isPresentValue());
    assertEquals("ordered", astType.getModifier().getStereotype().getValue(1).getName());
    assertFalse(astType.getModifier().getStereotype().getValue(1).isPresentValue());
  }
}
