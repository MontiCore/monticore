/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.mc2cd.symbolTransl;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.codegen.mc2cd.TranslationTestCase;
import de.monticore.umlstereotype._ast.ASTStereoValue;

import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;

public class SpanningScopeTest extends TranslationTestCase {

  /***
   * tests if in symbol cd the scope spanning symbols have the stereotype scope
   */

  private ASTCDCompilationUnit compilationUnit;

  @BeforeEach
  public void setUp() {
    compilationUnit = TestHelper.parseAndTransformForSymbol(Paths
        .get("src/test/resources/mc2cdtransformation/symbolTransl/ScopeSpanning.mc4")).get();
  }

  @Test
  public void testDefinitionName() {
    Assertions.assertEquals("ScopeSpanningSymbols", compilationUnit.getCDDefinition().getName());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testPackage() {
    Assertions.assertEquals(2, compilationUnit.getCDPackageList().size());
    Assertions.assertEquals("mc2cdtransformation", compilationUnit.getMCPackageDeclaration().getMCQualifiedName().getParts(0));
    Assertions.assertEquals("symbolTransl", compilationUnit.getMCPackageDeclaration().getMCQualifiedName().getParts(1));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testClassCount() {
    Assertions.assertEquals(2, compilationUnit.getCDDefinition().getCDClassesList().size());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testScopeSpanningSymbol() {
    ASTCDClass symbolClassSymbol = getClassBy("ScopeSpanning", compilationUnit);
    Assertions.assertTrue(symbolClassSymbol.getInterfaceList().isEmpty());
    Assertions.assertTrue(symbolClassSymbol.getCDMethodList().isEmpty());
    Assertions.assertTrue(symbolClassSymbol.getCDConstructorList().isEmpty());
    Assertions.assertFalse(symbolClassSymbol.isPresentCDExtendUsage());
    Assertions.assertTrue(symbolClassSymbol.getCDAttributeList().isEmpty());

    Assertions.assertTrue(symbolClassSymbol.getModifier().isPresentStereotype());
    Assertions.assertFalse(symbolClassSymbol.getModifier().getStereotype().isEmptyValues());
    Assertions.assertEquals(2, symbolClassSymbol.getModifier().getStereotype().sizeValues());
    ASTStereoValue symbolStereotype = symbolClassSymbol.getModifier().getStereotype().getValues(0);
    Assertions.assertEquals("symbol", symbolStereotype.getName());
    ASTStereoValue scopeStereotype = symbolClassSymbol.getModifier().getStereotype().getValues(1);
    Assertions.assertEquals("scope", scopeStereotype.getName());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testOnlySymbol() {
    ASTCDClass symbolClassSymbol = getClassBy("OnlySymbol", compilationUnit);
    Assertions.assertTrue(symbolClassSymbol.getInterfaceList().isEmpty());
    Assertions.assertTrue(symbolClassSymbol.getCDMethodList().isEmpty());
    Assertions.assertTrue(symbolClassSymbol.getCDConstructorList().isEmpty());
    Assertions.assertFalse(symbolClassSymbol.isPresentCDExtendUsage());
    Assertions.assertTrue(symbolClassSymbol.getCDAttributeList().isEmpty());

    Assertions.assertTrue(symbolClassSymbol.getModifier().isPresentStereotype());
    Assertions.assertFalse(symbolClassSymbol.getModifier().getStereotype().isEmptyValues());
    Assertions.assertEquals(2, symbolClassSymbol.getModifier().getStereotype().sizeValues());
    ASTStereoValue symbolStereotype = symbolClassSymbol.getModifier().getStereotype().getValues(0);
    Assertions.assertEquals("symbol", symbolStereotype.getName());
    ASTStereoValue startProdStereotype = symbolClassSymbol.getModifier().getStereotype().getValues(1);
    Assertions.assertEquals("startProd", startProdStereotype.getName());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
