/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.mc2cd.symbolTransl;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cd.facade.CDModifier;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.codegen.mc2cd.TranslationTestCase;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Paths;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static org.junit.Assert.*;

public class CDSymbolTranslationTest extends TranslationTestCase {

  private ASTCDCompilationUnit compilationUnit;

  @Before
  public void setUp() {
    compilationUnit = TestHelper.parseAndTransformForSymbol(Paths
        .get("src/test/resources/mc2cdtransformation/symbolTransl/SymbolRule.mc4")).get();
  }

  @Test
  public void testDefinitionName() {
    assertEquals("SymbolRuleSymbols", compilationUnit.getCDDefinition().getName());
  
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
    assertEquals(4, compilationUnit.getCDDefinition().getCDClassesList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testClassSymbol() {
    ASTCDClass symbolClassSymbol = getClassBy("SymbolClass", compilationUnit);
    assertEquals(1, symbolClassSymbol.getCDAttributeList().size());
    assertEquals(1, symbolClassSymbol.getInterfaceList().size());
    assertEquals(1, symbolClassSymbol.getCDMethodList().size());
    assertTrue(symbolClassSymbol.getCDConstructorList().isEmpty());
    assertTrue(symbolClassSymbol.isPresentCDExtendUsage());

    ASTCDAttribute cdAttribute = symbolClassSymbol.getCDAttributeList().get(0);
    assertEquals("extraString", cdAttribute.getName());
    assertDeepEquals(String.class, cdAttribute.getMCType());
    assertDeepEquals(CDModifier.PROTECTED, cdAttribute.getModifier());

    ASTCDMethod cdMethod = symbolClassSymbol.getCDMethodList().get(0);
    assertEquals("toString", cdMethod.getName());
    assertTrue(cdMethod.getMCReturnType().isPresentMCType());
    assertDeepEquals(String.class, cdMethod.getMCReturnType().getMCType());
    assertTrue(cdMethod.getModifier().isPublic());
    assertTrue(cdMethod.getModifier().isPresentStereotype());
    assertEquals(1, cdMethod.getModifier().getStereotype().sizeValues());
    assertEquals("methodBody", cdMethod.getModifier().getStereotype().getValues(0).getName());

    ASTMCObjectType cdInterface = symbolClassSymbol.getInterfaceList().get(0);
    assertDeepEquals("de.monticore.symboltable.ISymbol", cdInterface);

    ASTMCObjectType superclass = symbolClassSymbol.getCDExtendUsage().getSuperclass(0);
    assertDeepEquals("de.monticore.symboltable.Symbol", superclass);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAbstractClassSymbol() {
    ASTCDClass symbolClassSymbol = getClassBy("SymbolAbstractClass", compilationUnit);
    assertEquals(1, symbolClassSymbol.getCDAttributeList().size());
    assertTrue(symbolClassSymbol.getInterfaceList().isEmpty());
    assertTrue(symbolClassSymbol.getCDMethodList().isEmpty());
    assertTrue(symbolClassSymbol.getCDConstructorList().isEmpty());
    assertFalse(symbolClassSymbol.isPresentCDExtendUsage());

    ASTCDAttribute cdAttribute = symbolClassSymbol.getCDAttributeList().get(0);
    assertEquals("extraString", cdAttribute.getName());
    assertDeepEquals(String.class, cdAttribute.getMCType());
    assertDeepEquals(CDModifier.PROTECTED, cdAttribute.getModifier());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInterfaceCount() {
    assertTrue(compilationUnit.getCDDefinition().getCDInterfacesList().isEmpty());
  
    assertTrue(Log.getFindings().isEmpty());

  }

  @Test
  public void testInterfaceSymbol() {
    ASTCDClass symbolClassSymbol = getClassBy("SymbolInterface", compilationUnit);
    assertEquals(1, symbolClassSymbol.getCDAttributeList().size());
    assertTrue(symbolClassSymbol.getInterfaceList().isEmpty());
    assertTrue(symbolClassSymbol.getCDMethodList().isEmpty());

    ASTCDAttribute cdAttribute = symbolClassSymbol.getCDAttributeList().get(0);
    assertEquals("extraString", cdAttribute.getName());
    assertDeepEquals(String.class, cdAttribute.getMCType());
    assertDeepEquals(CDModifier.PROTECTED, cdAttribute.getModifier());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testExternalSymbol() {
    ASTCDClass symbolClassSymbol = getClassBy("SymbolExternal", compilationUnit);
    assertEquals(1, symbolClassSymbol.getCDAttributeList().size());
    assertTrue(symbolClassSymbol.getInterfaceList().isEmpty());
    assertTrue(symbolClassSymbol.getCDMethodList().isEmpty());

    ASTCDAttribute cdAttribute = symbolClassSymbol.getCDAttributeList().get(0);
    assertEquals("extraString", cdAttribute.getName());
    assertDeepEquals(String.class, cdAttribute.getMCType());
    assertDeepEquals(CDModifier.PROTECTED, cdAttribute.getModifier());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}


