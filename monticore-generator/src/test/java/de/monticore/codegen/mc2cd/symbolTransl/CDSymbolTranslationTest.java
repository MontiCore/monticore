/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.mc2cd.symbolTransl;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cd.facade.CDModifier;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.codegen.mc2cd.TranslationTestCase;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;

public class CDSymbolTranslationTest extends TranslationTestCase {

  private ASTCDCompilationUnit compilationUnit;

  @BeforeEach
  public void setUp() {
    compilationUnit = TestHelper.parseAndTransformForSymbol(Paths
        .get("src/test/resources/mc2cdtransformation/symbolTransl/SymbolRule.mc4")).get();
  }

  @Test
  public void testDefinitionName() {
    Assertions.assertEquals("SymbolRuleSymbols", compilationUnit.getCDDefinition().getName());
  
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
    Assertions.assertEquals(4, compilationUnit.getCDDefinition().getCDClassesList().size());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testClassSymbol() {
    ASTCDClass symbolClassSymbol = getClassBy("SymbolClass", compilationUnit);
    Assertions.assertEquals(1, symbolClassSymbol.getCDAttributeList().size());
    Assertions.assertEquals(1, symbolClassSymbol.getInterfaceList().size());
    Assertions.assertEquals(1, symbolClassSymbol.getCDMethodList().size());
    Assertions.assertTrue(symbolClassSymbol.getCDConstructorList().isEmpty());
    Assertions.assertTrue(symbolClassSymbol.isPresentCDExtendUsage());

    ASTCDAttribute cdAttribute = symbolClassSymbol.getCDAttributeList().get(0);
    Assertions.assertEquals("extraString", cdAttribute.getName());
    assertDeepEquals(String.class, cdAttribute.getMCType());
    assertDeepEquals(CDModifier.PROTECTED, cdAttribute.getModifier());

    ASTCDMethod cdMethod = symbolClassSymbol.getCDMethodList().get(0);
    Assertions.assertEquals("toString", cdMethod.getName());
    Assertions.assertTrue(cdMethod.getMCReturnType().isPresentMCType());
    assertDeepEquals(String.class, cdMethod.getMCReturnType().getMCType());
    Assertions.assertTrue(cdMethod.getModifier().isPublic());
    Assertions.assertTrue(cdMethod.getModifier().isPresentStereotype());
    Assertions.assertEquals(1, cdMethod.getModifier().getStereotype().sizeValues());
    Assertions.assertEquals("methodBody", cdMethod.getModifier().getStereotype().getValues(0).getName());

    ASTMCObjectType cdInterface = symbolClassSymbol.getInterfaceList().get(0);
    assertDeepEquals("de.monticore.symboltable.ISymbol", cdInterface);

    ASTMCObjectType superclass = symbolClassSymbol.getCDExtendUsage().getSuperclass(0);
    assertDeepEquals("de.monticore.symboltable.Symbol", superclass);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAbstractClassSymbol() {
    ASTCDClass symbolClassSymbol = getClassBy("SymbolAbstractClass", compilationUnit);
    Assertions.assertEquals(1, symbolClassSymbol.getCDAttributeList().size());
    Assertions.assertTrue(symbolClassSymbol.getInterfaceList().isEmpty());
    Assertions.assertTrue(symbolClassSymbol.getCDMethodList().isEmpty());
    Assertions.assertTrue(symbolClassSymbol.getCDConstructorList().isEmpty());
    Assertions.assertFalse(symbolClassSymbol.isPresentCDExtendUsage());

    ASTCDAttribute cdAttribute = symbolClassSymbol.getCDAttributeList().get(0);
    Assertions.assertEquals("extraString", cdAttribute.getName());
    assertDeepEquals(String.class, cdAttribute.getMCType());
    assertDeepEquals(CDModifier.PROTECTED, cdAttribute.getModifier());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInterfaceCount() {
    Assertions.assertTrue(compilationUnit.getCDDefinition().getCDInterfacesList().isEmpty());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());

  }

  @Test
  public void testInterfaceSymbol() {
    ASTCDClass symbolClassSymbol = getClassBy("SymbolInterface", compilationUnit);
    Assertions.assertEquals(1, symbolClassSymbol.getCDAttributeList().size());
    Assertions.assertTrue(symbolClassSymbol.getInterfaceList().isEmpty());
    Assertions.assertTrue(symbolClassSymbol.getCDMethodList().isEmpty());

    ASTCDAttribute cdAttribute = symbolClassSymbol.getCDAttributeList().get(0);
    Assertions.assertEquals("extraString", cdAttribute.getName());
    assertDeepEquals(String.class, cdAttribute.getMCType());
    assertDeepEquals(CDModifier.PROTECTED, cdAttribute.getModifier());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testExternalSymbol() {
    ASTCDClass symbolClassSymbol = getClassBy("SymbolExternal", compilationUnit);
    Assertions.assertEquals(1, symbolClassSymbol.getCDAttributeList().size());
    Assertions.assertTrue(symbolClassSymbol.getInterfaceList().isEmpty());
    Assertions.assertTrue(symbolClassSymbol.getCDMethodList().isEmpty());

    ASTCDAttribute cdAttribute = symbolClassSymbol.getCDAttributeList().get(0);
    Assertions.assertEquals("extraString", cdAttribute.getName());
    assertDeepEquals(String.class, cdAttribute.getMCType());
    assertDeepEquals(CDModifier.PROTECTED, cdAttribute.getModifier());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}


