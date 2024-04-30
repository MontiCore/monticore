/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.mc2cd.symbolTransl;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.codegen.mc2cd.TranslationTestCase;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.umlstereotype._ast.ASTStereoValue;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Paths;

import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static org.junit.Assert.*;

public class SpanningScopeTest extends TranslationTestCase {

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
    assertEquals("ScopeSpanningSymbols", compilationUnit.getCDDefinition().getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testPackage() {
    assertEquals(2, compilationUnit.getCDPackageList().size());
    assertEquals("mc2cdtransformation", compilationUnit.getMCPackageDeclaration().getMCQualifiedName().getParts(0));
    assertEquals("symbolTransl", compilationUnit.getMCPackageDeclaration().getMCQualifiedName().getParts(1));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testClassCount() {
    assertEquals(2, compilationUnit.getCDDefinition().getCDClassesList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testScopeSpanningSymbol() {
    ASTCDClass symbolClassSymbol = getClassBy("ScopeSpanning", compilationUnit);
    assertTrue(symbolClassSymbol.getInterfaceList().isEmpty());
    assertTrue(symbolClassSymbol.getCDMethodList().isEmpty());
    assertTrue(symbolClassSymbol.getCDConstructorList().isEmpty());
    assertFalse(symbolClassSymbol.isPresentCDExtendUsage());
    assertTrue(symbolClassSymbol.getCDAttributeList().isEmpty());

    assertTrue(symbolClassSymbol.getModifier().isPresentStereotype());
    assertFalse(symbolClassSymbol.getModifier().getStereotype().isEmptyValues());
    assertEquals(2, symbolClassSymbol.getModifier().getStereotype().sizeValues());
    ASTStereoValue symbolStereotype = symbolClassSymbol.getModifier().getStereotype().getValues(0);
    assertEquals("symbol", symbolStereotype.getName());
    ASTStereoValue scopeStereotype = symbolClassSymbol.getModifier().getStereotype().getValues(1);
    assertEquals("scope", scopeStereotype.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testOnlySymbol() {
    ASTCDClass symbolClassSymbol = getClassBy("OnlySymbol", compilationUnit);
    assertTrue(symbolClassSymbol.getInterfaceList().isEmpty());
    assertTrue(symbolClassSymbol.getCDMethodList().isEmpty());
    assertTrue(symbolClassSymbol.getCDConstructorList().isEmpty());
    assertFalse(symbolClassSymbol.isPresentCDExtendUsage());
    assertTrue(symbolClassSymbol.getCDAttributeList().isEmpty());

    assertTrue(symbolClassSymbol.getModifier().isPresentStereotype());
    assertFalse(symbolClassSymbol.getModifier().getStereotype().isEmptyValues());
    assertEquals(2, symbolClassSymbol.getModifier().getStereotype().sizeValues());
    ASTStereoValue symbolStereotype = symbolClassSymbol.getModifier().getStereotype().getValues(0);
    assertEquals("symbol", symbolStereotype.getName());
    ASTStereoValue startProdStereotype = symbolClassSymbol.getModifier().getStereotype().getValues(1);
    assertEquals("startProd", startProdStereotype.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
