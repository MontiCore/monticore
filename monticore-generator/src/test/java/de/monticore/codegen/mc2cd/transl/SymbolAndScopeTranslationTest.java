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
    assertEquals(1, astType.getModifier().getStereotype().getValueList().size());
    assertEquals("inheritedSymbol", astType.getModifier().getStereotype().getValue(0).getName());
    assertTrue(astType.getModifier().getStereotype().getValue(0).isPresentValue());
    assertEquals("mc2cdtransformation.symboltransl.symbolrule._symboltable.SymbolClassSymbol",
        astType.getModifier().getStereotype().getValue(0).getValue());
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
    ASTCDClass astType = getClassBy("ASTScopeClass", symbolCD);
    assertTrue(astType.isPresentModifier());
    assertTrue(astType.getModifier().isPresentStereotype());
    assertEquals(1, astType.getModifier().getStereotype().getValueList().size());
    assertEquals("scope", astType.getModifier().getStereotype().getValue(0).getName());
    assertFalse(astType.getModifier().getStereotype().getValue(0).isPresentValue());
  }
}
