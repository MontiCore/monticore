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

public class SymbolAndScopeTranslationTest extends TranslationTestCase {

  private ASTCDCompilationUnit symbolCD;

  @BeforeEach
  public void setUpSymbolAndScopeTranslationTest() {
    symbolCD = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/SymbolAndScopeTranslation.mc4")).get();
  }

  /**
   * symbol test
   */
  @Test
  public void testSimpleSymbolInterface() {
    ASTCDInterface astType = getInterfaceBy("ASTType", symbolCD);
    Assertions.assertTrue(astType.getModifier().isPresentStereotype());
    // only 2 because other one is the start prod stereotype
    Assertions.assertEquals(2, astType.getModifier().getStereotype().getValuesList().size());
    Assertions.assertEquals("symbol", astType.getModifier().getStereotype().getValues(0).getName());
    Assertions.assertTrue(astType.getModifier().getStereotype().getValues(0).getValue().isEmpty());
    Assertions.assertEquals("startProd", astType.getModifier().getStereotype().getValues(1).getName());
    Assertions.assertTrue(astType.getModifier().getStereotype().getValues(1).getValue().isEmpty());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInheritedSymbolInterface_OneLevel() {
    ASTCDInterface astType = getInterfaceBy("ASTCDType", symbolCD);
    Assertions.assertTrue(astType.getModifier().isPresentStereotype());

    Assertions.assertEquals(1, astType.getModifier().getStereotype().getValuesList().size());
    Assertions.assertEquals("inheritedSymbol", astType.getModifier().getStereotype().getValues(0).getName());
    Assertions.assertFalse(astType.getModifier().getStereotype().getValues(0).getValue().isEmpty());
    Assertions.assertEquals("mc2cdtransformation.symbolandscopetranslation._symboltable.TypeSymbol", astType.getModifier().getStereotype().getValues(0).getValue());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInheritedSymbolInterface_TwoLevels() {
    ASTCDClass astType = getClassBy("ASTCDClass", symbolCD);
    Assertions.assertTrue(astType.getModifier().isPresentStereotype());

    Assertions.assertEquals(1, astType.getModifier().getStereotype().getValuesList().size());
    Assertions.assertEquals("inheritedSymbol", astType.getModifier().getStereotype().getValues(0).getName());
    Assertions.assertFalse(astType.getModifier().getStereotype().getValues(0).getValue().isEmpty());
    Assertions.assertEquals("mc2cdtransformation.symbolandscopetranslation._symboltable.TypeSymbol", astType.getModifier().getStereotype().getValues(0).getValue());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSimpleSymbolClass() {
    ASTCDClass astType = getClassBy("ASTSimpleSymbolClass", symbolCD);
    Assertions.assertTrue(astType.getModifier().isPresentStereotype());
    Assertions.assertEquals(1, astType.getModifier().getStereotype().getValuesList().size());
    Assertions.assertEquals("symbol", astType.getModifier().getStereotype().getValues(0).getName());
    Assertions.assertTrue(astType.getModifier().getStereotype().getValues(0).getValue().isEmpty());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSymbolClassOverwritten() {
    ASTCDClass astType = getClassBy("ASTSymbolClass", symbolCD);
    Assertions.assertTrue(astType.getModifier().isPresentStereotype());
    Assertions.assertEquals(2, astType.getModifier().getStereotype().getValuesList().size());
    Assertions.assertEquals("inheritedSymbol", astType.getModifier().getStereotype().getValues(0).getName());
    Assertions.assertFalse(astType.getModifier().getStereotype().getValues(0).getValue().isEmpty());
    Assertions.assertEquals("mc2cdtransformation.symboltransl.symbolrule._symboltable.SymbolClassSymbol", astType.getModifier().getStereotype().getValues(0).getValue());

    Assertions.assertEquals("inheritedScope", astType.getModifier().getStereotype().getValues(1).getName());
    Assertions.assertTrue(astType.getModifier().getStereotype().getValues(1).getValue().isEmpty());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSymbolClassExtents() {
    ASTCDClass astType = getClassBy("ASTExtentsSymbolClass", symbolCD);
    Assertions.assertTrue(astType.getModifier().isPresentStereotype());
    Assertions.assertEquals(2, astType.getModifier().getStereotype().getValuesList().size());
    Assertions.assertEquals("inheritedSymbol", astType.getModifier().getStereotype().getValues(0).getName());
    Assertions.assertFalse(astType.getModifier().getStereotype().getValues(0).getValue().isEmpty());
    Assertions.assertEquals("mc2cdtransformation.symbolandscopetranslation._symboltable.FooSymbol", astType.getModifier().getStereotype().getValues(0).getValue());

    Assertions.assertEquals("inheritedScope", astType.getModifier().getStereotype().getValues(1).getName());
    Assertions.assertTrue(astType.getModifier().getStereotype().getValues(1).getValue().isEmpty());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSymbolInterfaceOverwritten() {
    ASTCDClass astType = getClassBy("ASTSymbolInterface", symbolCD);
    Assertions.assertTrue(astType.getModifier().isPresentStereotype());
    Assertions.assertEquals(1, astType.getModifier().getStereotype().getValuesList().size());
    Assertions.assertEquals("inheritedSymbol", astType.getModifier().getStereotype().getValues(0).getName());
    Assertions.assertFalse(astType.getModifier().getStereotype().getValues(0).getValue().isEmpty());
    Assertions.assertEquals("mc2cdtransformation.symboltransl.symbolrule._symboltable.SymbolInterfaceSymbol", astType.getModifier().getStereotype().getValues(0).getValue());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSymbolInterfaceImplements() {
    ASTCDClass astType = getClassBy("ASTImplementsSymbolInterface", symbolCD);
    Assertions.assertTrue(astType.getModifier().isPresentStereotype());
    Assertions.assertEquals(2, astType.getModifier().getStereotype().getValuesList().size());
    Assertions.assertEquals("inheritedSymbol", astType.getModifier().getStereotype().getValues(0).getName());
    Assertions.assertFalse(astType.getModifier().getStereotype().getValues(0).getValue().isEmpty());
    Assertions.assertEquals("mc2cdtransformation.symbolandscopetranslation._symboltable.InterfaceFooSymbol", astType.getModifier().getStereotype().getValues(0).getValue());

    Assertions.assertEquals("inheritedScope", astType.getModifier().getStereotype().getValues(1).getName());
    Assertions.assertTrue(astType.getModifier().getStereotype().getValues(1).getValue().isEmpty());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSymbolAbstractClassOverwritten() {
    ASTCDClass astType = getClassBy("ASTSymbolAbstractClass", symbolCD);
    Assertions.assertTrue(astType.getModifier().isPresentStereotype());
    Assertions.assertEquals(1, astType.getModifier().getStereotype().getValuesList().size());
    Assertions.assertEquals("inheritedSymbol", astType.getModifier().getStereotype().getValues(0).getName());
    Assertions.assertFalse(astType.getModifier().getStereotype().getValues(0).getValue().isEmpty());
    Assertions.assertEquals("mc2cdtransformation.symboltransl.symbolrule._symboltable.SymbolAbstractClassSymbol", astType.getModifier().getStereotype().getValues(0).getValue());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSymbolAbstractClassExtents() {
    ASTCDClass astType = getClassBy("ASTExtentsSymbolAbstractClass", symbolCD);
    Assertions.assertTrue(astType.getModifier().isPresentStereotype());
    Assertions.assertEquals(2, astType.getModifier().getStereotype().getValuesList().size());
    Assertions.assertEquals("inheritedSymbol", astType.getModifier().getStereotype().getValues(0).getName());
    Assertions.assertFalse(astType.getModifier().getStereotype().getValues(0).getValue().isEmpty());
    Assertions.assertEquals("mc2cdtransformation.symbolandscopetranslation._symboltable.AbstractFooSymbol", astType.getModifier().getStereotype().getValues(0).getValue());

    Assertions.assertEquals("inheritedScope", astType.getModifier().getStereotype().getValues(1).getName());
    Assertions.assertTrue(astType.getModifier().getStereotype().getValues(1).getValue().isEmpty());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  /**
   * scope test
   */
  @Test
  public void testSimpleScopeInterface() {
    ASTCDInterface astType = getInterfaceBy("ASTTypeScope", symbolCD);
    Assertions.assertTrue(astType.getModifier().isPresentStereotype());
    Assertions.assertEquals(1, astType.getModifier().getStereotype().getValuesList().size());
    Assertions.assertEquals("scope", astType.getModifier().getStereotype().getValues(0).getName());
    Assertions.assertTrue(astType.getModifier().getStereotype().getValues(0).getValue().isEmpty());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInheritedScopeInterface_OneLevel() {
    ASTCDInterface astType = getInterfaceBy("ASTCDTypeScope", symbolCD);
    Assertions.assertTrue(astType.getModifier().isPresentStereotype());

    Assertions.assertEquals(1, astType.getModifier().getStereotype().getValuesList().size());
    Assertions.assertEquals("inheritedScope", astType.getModifier().getStereotype().getValues(0).getName());
    Assertions.assertTrue(astType.getModifier().getStereotype().getValues(0).getValue().isEmpty());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInheritedScopeInterface_TwoLevels() {
    ASTCDClass astType = getClassBy("ASTCDClassScope", symbolCD);
    Assertions.assertTrue(astType.getModifier().isPresentStereotype());

    Assertions.assertEquals(1, astType.getModifier().getStereotype().getValuesList().size());
    Assertions.assertEquals("inheritedScope", astType.getModifier().getStereotype().getValues(0).getName());
    Assertions.assertTrue(astType.getModifier().getStereotype().getValues(0).getValue().isEmpty());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSimpleScopeClass() {
    ASTCDClass astType = getClassBy("ASTSimpleScopeClass", symbolCD);
    Assertions.assertTrue(astType.getModifier().isPresentStereotype());
    Assertions.assertEquals(1, astType.getModifier().getStereotype().getValuesList().size());
    Assertions.assertEquals("scope", astType.getModifier().getStereotype().getValues(0).getName());
    Assertions.assertTrue(astType.getModifier().getStereotype().getValues(0).getValue().isEmpty());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testNoShadowingScopeClass() {
    ASTCDClass astType = getClassBy("ASTScopeShadowing", symbolCD);
    Assertions.assertTrue(astType.getModifier().isPresentStereotype());
    Assertions.assertEquals(2, astType.getModifier().getStereotype().getValuesList().size());
    Assertions.assertEquals("scope", astType.getModifier().getStereotype().getValues(0).getName());
    Assertions.assertTrue(astType.getModifier().getStereotype().getValues(0).getValue().isEmpty());
    Assertions.assertEquals("shadowing", astType.getModifier().getStereotype().getValues(1).getName());
    Assertions.assertTrue(astType.getModifier().getStereotype().getValues(1).getValue().isEmpty());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testNoExportingScopeClass() {
    ASTCDClass astType = getClassBy("ASTScopeNonExporting", symbolCD);
    Assertions.assertTrue(astType.getModifier().isPresentStereotype());
    Assertions.assertEquals(2, astType.getModifier().getStereotype().getValuesList().size());
    Assertions.assertEquals("scope", astType.getModifier().getStereotype().getValues(0).getName());
    Assertions.assertTrue(astType.getModifier().getStereotype().getValues(0).getValue().isEmpty());
    Assertions.assertEquals("non_exporting", astType.getModifier().getStereotype().getValues(1).getName());
    Assertions.assertTrue(astType.getModifier().getStereotype().getValues(1).getValue().isEmpty());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testOrderedScopeClass() {
    ASTCDClass astType = getClassBy("ASTScopeOrdered", symbolCD);
    Assertions.assertTrue(astType.getModifier().isPresentStereotype());
    Assertions.assertEquals(2, astType.getModifier().getStereotype().getValuesList().size());
    Assertions.assertEquals("scope", astType.getModifier().getStereotype().getValues(0).getName());
    Assertions.assertTrue(astType.getModifier().getStereotype().getValues(0).getValue().isEmpty());
    Assertions.assertEquals("ordered", astType.getModifier().getStereotype().getValues(1).getName());
    Assertions.assertTrue(astType.getModifier().getStereotype().getValues(1).getValue().isEmpty());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
