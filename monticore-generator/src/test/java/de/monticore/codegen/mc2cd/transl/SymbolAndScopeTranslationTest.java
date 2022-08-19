/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.mc2cd.transl;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.grammar.grammarfamily.GrammarFamilyMill;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Paths;

import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getInterfaceBy;
import static org.junit.Assert.*;

public class SymbolAndScopeTranslationTest {

  private ASTCDCompilationUnit symbolCD;

  @Before
  public void setup(){
    GrammarFamilyMill.init();
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Before
  public void setUp() {
    LogStub.init();         // replace log by a sideffect free variant
    // LogStub.initPlusLog();  // for manual testing purpose only
    Log.enableFailQuick(false);
    symbolCD = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/SymbolAndScopeTranslation.mc4")).get();
  }

  /**
   * symbol test
   */
  @Test
  public void testSimpleSymbolInterface() {
    ASTCDInterface astType = getInterfaceBy("ASTType", symbolCD);
    assertTrue(astType.getModifier().isPresentStereotype());
    // only 2 because other one is the start prod stereotype
    assertEquals(2, astType.getModifier().getStereotype().getValuesList().size());
    assertEquals("symbol", astType.getModifier().getStereotype().getValues(0).getName());
    assertTrue(astType.getModifier().getStereotype().getValues(0).getValue().isEmpty());
    assertEquals("startProd", astType.getModifier().getStereotype().getValues(1).getName());
    assertTrue(astType.getModifier().getStereotype().getValues(1).getValue().isEmpty());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInheritedSymbolInterface_OneLevel() {
    ASTCDInterface astType = getInterfaceBy("ASTCDType", symbolCD);
    assertTrue(astType.getModifier().isPresentStereotype());

    assertEquals(1, astType.getModifier().getStereotype().getValuesList().size());
    assertEquals("inheritedSymbol", astType.getModifier().getStereotype().getValues(0).getName());
    assertFalse(astType.getModifier().getStereotype().getValues(0).getValue().isEmpty());
    assertEquals("mc2cdtransformation.symbolandscopetranslation._symboltable.TypeSymbol",
        astType.getModifier().getStereotype().getValues(0).getValue());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInheritedSymbolInterface_TwoLevels() {
    ASTCDClass astType = getClassBy("ASTCDClass", symbolCD);
    assertTrue(astType.getModifier().isPresentStereotype());

    assertEquals(1, astType.getModifier().getStereotype().getValuesList().size());
    assertEquals("inheritedSymbol", astType.getModifier().getStereotype().getValues(0).getName());
    assertFalse(astType.getModifier().getStereotype().getValues(0).getValue().isEmpty());
    assertEquals("mc2cdtransformation.symbolandscopetranslation._symboltable.TypeSymbol",
        astType.getModifier().getStereotype().getValues(0).getValue());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSimpleSymbolClass() {
    ASTCDClass astType = getClassBy("ASTSimpleSymbolClass", symbolCD);
    assertTrue(astType.getModifier().isPresentStereotype());
    assertEquals(1, astType.getModifier().getStereotype().getValuesList().size());
    assertEquals("symbol", astType.getModifier().getStereotype().getValues(0).getName());
    assertTrue(astType.getModifier().getStereotype().getValues(0).getValue().isEmpty());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSymbolClassOverwritten() {
    ASTCDClass astType = getClassBy("ASTSymbolClass", symbolCD);
    assertTrue(astType.getModifier().isPresentStereotype());
    assertEquals(2, astType.getModifier().getStereotype().getValuesList().size());
    assertEquals("inheritedSymbol", astType.getModifier().getStereotype().getValues(0).getName());
    assertFalse(astType.getModifier().getStereotype().getValues(0).getValue().isEmpty());
    assertEquals("mc2cdtransformation.symboltransl.symbolrule._symboltable.SymbolClassSymbol",
        astType.getModifier().getStereotype().getValues(0).getValue());

    assertEquals("inheritedScope", astType.getModifier().getStereotype().getValues(1).getName());
    assertTrue(astType.getModifier().getStereotype().getValues(1).getValue().isEmpty());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSymbolClassExtents() {
    ASTCDClass astType = getClassBy("ASTExtentsSymbolClass", symbolCD);
    assertTrue(astType.getModifier().isPresentStereotype());
    assertEquals(2, astType.getModifier().getStereotype().getValuesList().size());
    assertEquals("inheritedSymbol", astType.getModifier().getStereotype().getValues(0).getName());
    assertFalse(astType.getModifier().getStereotype().getValues(0).getValue().isEmpty());
    assertEquals("mc2cdtransformation.symbolandscopetranslation._symboltable.FooSymbol",
        astType.getModifier().getStereotype().getValues(0).getValue());

    assertEquals("inheritedScope", astType.getModifier().getStereotype().getValues(1).getName());
    assertTrue(astType.getModifier().getStereotype().getValues(1).getValue().isEmpty());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSymbolInterfaceOverwritten() {
    ASTCDClass astType = getClassBy("ASTSymbolInterface", symbolCD);
    assertTrue(astType.getModifier().isPresentStereotype());
    assertEquals(1, astType.getModifier().getStereotype().getValuesList().size());
    assertEquals("inheritedSymbol", astType.getModifier().getStereotype().getValues(0).getName());
    assertFalse(astType.getModifier().getStereotype().getValues(0).getValue().isEmpty());
    assertEquals("mc2cdtransformation.symboltransl.symbolrule._symboltable.SymbolInterfaceSymbol",
        astType.getModifier().getStereotype().getValues(0).getValue());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSymbolInterfaceImplements() {
    ASTCDClass astType = getClassBy("ASTImplementsSymbolInterface", symbolCD);
    assertTrue(astType.getModifier().isPresentStereotype());
    assertEquals(2, astType.getModifier().getStereotype().getValuesList().size());
    assertEquals("inheritedSymbol", astType.getModifier().getStereotype().getValues(0).getName());
    assertFalse(astType.getModifier().getStereotype().getValues(0).getValue().isEmpty());
    assertEquals("mc2cdtransformation.symbolandscopetranslation._symboltable.InterfaceFooSymbol",
        astType.getModifier().getStereotype().getValues(0).getValue());

    assertEquals("inheritedScope", astType.getModifier().getStereotype().getValues(1).getName());
    assertTrue(astType.getModifier().getStereotype().getValues(1).getValue().isEmpty());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSymbolAbstractClassOverwritten() {
    ASTCDClass astType = getClassBy("ASTSymbolAbstractClass", symbolCD);
    assertTrue(astType.getModifier().isPresentStereotype());
    assertEquals(1, astType.getModifier().getStereotype().getValuesList().size());
    assertEquals("inheritedSymbol", astType.getModifier().getStereotype().getValues(0).getName());
    assertFalse(astType.getModifier().getStereotype().getValues(0).getValue().isEmpty());
    assertEquals("mc2cdtransformation.symboltransl.symbolrule._symboltable.SymbolAbstractClassSymbol",
        astType.getModifier().getStereotype().getValues(0).getValue());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSymbolAbstractClassExtents() {
    ASTCDClass astType = getClassBy("ASTExtentsSymbolAbstractClass", symbolCD);
    assertTrue(astType.getModifier().isPresentStereotype());
    assertEquals(2, astType.getModifier().getStereotype().getValuesList().size());
    assertEquals("inheritedSymbol", astType.getModifier().getStereotype().getValues(0).getName());
    assertFalse(astType.getModifier().getStereotype().getValues(0).getValue().isEmpty());
    assertEquals("mc2cdtransformation.symbolandscopetranslation._symboltable.AbstractFooSymbol",
        astType.getModifier().getStereotype().getValues(0).getValue());

    assertEquals("inheritedScope", astType.getModifier().getStereotype().getValues(1).getName());
    assertTrue(astType.getModifier().getStereotype().getValues(1).getValue().isEmpty());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  /**
   * scope test
   */
  @Test
  public void testSimpleScopeInterface() {
    ASTCDInterface astType = getInterfaceBy("ASTTypeScope", symbolCD);
    assertTrue(astType.getModifier().isPresentStereotype());
    assertEquals(1, astType.getModifier().getStereotype().getValuesList().size());
    assertEquals("scope", astType.getModifier().getStereotype().getValues(0).getName());
    assertTrue(astType.getModifier().getStereotype().getValues(0).getValue().isEmpty());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInheritedScopeInterface_OneLevel() {
    ASTCDInterface astType = getInterfaceBy("ASTCDTypeScope", symbolCD);
    assertTrue(astType.getModifier().isPresentStereotype());

    assertEquals(1, astType.getModifier().getStereotype().getValuesList().size());
    assertEquals("inheritedScope", astType.getModifier().getStereotype().getValues(0).getName());
    assertTrue(astType.getModifier().getStereotype().getValues(0).getValue().isEmpty());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInheritedScopeInterface_TwoLevels() {
    ASTCDClass astType = getClassBy("ASTCDClassScope", symbolCD);
    assertTrue(astType.getModifier().isPresentStereotype());

    assertEquals(1, astType.getModifier().getStereotype().getValuesList().size());
    assertEquals("inheritedScope", astType.getModifier().getStereotype().getValues(0).getName());
    assertTrue(astType.getModifier().getStereotype().getValues(0).getValue().isEmpty());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSimpleScopeClass() {
    ASTCDClass astType = getClassBy("ASTSimpleScopeClass", symbolCD);
    assertTrue(astType.getModifier().isPresentStereotype());
    assertEquals(1, astType.getModifier().getStereotype().getValuesList().size());
    assertEquals("scope", astType.getModifier().getStereotype().getValues(0).getName());
    assertTrue(astType.getModifier().getStereotype().getValues(0).getValue().isEmpty());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testNoShadowingScopeClass() {
    ASTCDClass astType = getClassBy("ASTScopeShadowing", symbolCD);
    assertTrue(astType.getModifier().isPresentStereotype());
    assertEquals(2, astType.getModifier().getStereotype().getValuesList().size());
    assertEquals("scope", astType.getModifier().getStereotype().getValues(0).getName());
    assertTrue(astType.getModifier().getStereotype().getValues(0).getValue().isEmpty());
    assertEquals("shadowing", astType.getModifier().getStereotype().getValues(1).getName());
    assertTrue(astType.getModifier().getStereotype().getValues(1).getValue().isEmpty());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testNoExportingScopeClass() {
    ASTCDClass astType = getClassBy("ASTScopeNonExporting", symbolCD);
    assertTrue(astType.getModifier().isPresentStereotype());
    assertEquals(2, astType.getModifier().getStereotype().getValuesList().size());
    assertEquals("scope", astType.getModifier().getStereotype().getValues(0).getName());
    assertTrue(astType.getModifier().getStereotype().getValues(0).getValue().isEmpty());
    assertEquals("non_exporting", astType.getModifier().getStereotype().getValues(1).getName());
    assertTrue(astType.getModifier().getStereotype().getValues(1).getValue().isEmpty());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testOrderedScopeClass() {
    ASTCDClass astType = getClassBy("ASTScopeOrdered", symbolCD);
    assertTrue(astType.getModifier().isPresentStereotype());
    assertEquals(2, astType.getModifier().getStereotype().getValuesList().size());
    assertEquals("scope", astType.getModifier().getStereotype().getValues(0).getName());
    assertTrue(astType.getModifier().getStereotype().getValues(0).getValue().isEmpty());
    assertEquals("ordered", astType.getModifier().getStereotype().getValues(1).getName());
    assertTrue(astType.getModifier().getStereotype().getValues(1).getValue().isEmpty());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
