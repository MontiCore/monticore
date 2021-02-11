/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.mc2cd.scopeTransl;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.CDModifier;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.grammar.grammarfamily.GrammarFamilyMill;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Paths;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CDScopeTranslationTest {

  private ASTCDCompilationUnit compilationUnit;

  @BeforeClass
  public static void setup(){
    GrammarFamilyMill.init();
  }

  @Before
  public void setUp() {
    compilationUnit = TestHelper.parseAndTransformForScope(Paths
        .get("src/test/resources/mc2cdtransformation/scopeTransl/ScopeRule.mc4")).get();
  }

  @Test
  public void testDefinitionName() {
    assertEquals("ScopeRuleScope", compilationUnit.getCDDefinition().getName());
  }

  @Test
  public void testPackage() {
    assertEquals(2, compilationUnit.getPackageList().size());
    assertEquals("mc2cdtransformation", compilationUnit.getPackage(0));
    assertEquals("scopeTransl", compilationUnit.getPackage(1));
  }

  @Test
  public void testClassCount() {
    assertEquals(1, compilationUnit.getCDDefinition().sizeCDClasses());
  }

  @Test
  public void testClassSymbol() {
    ASTCDClass scopeClass = getClassBy("ScopeRule", compilationUnit);
    assertEquals(1, scopeClass.sizeCDAttributes());
    assertEquals(1, scopeClass.sizeCDMethods());
    assertEquals(1, scopeClass.sizeInterface());

    assertTrue(scopeClass.isEmptyCDConstructors());
    assertTrue(scopeClass.isPresentSuperclass());

    ASTCDAttribute cdAttribute = scopeClass.getCDAttribute(0);
    assertEquals("extraAttr", cdAttribute.getName());
    assertDeepEquals(String.class, cdAttribute.getMCType());
    assertTrue(cdAttribute.isPresentModifier());
    assertDeepEquals(CDModifier.PROTECTED, cdAttribute.getModifier());

    ASTCDMethod cdMethod = scopeClass.getCDMethod(0);
    assertEquals("toString", cdMethod.getName());
    assertTrue(cdMethod.getMCReturnType().isPresentMCType());
    assertDeepEquals(String.class, cdMethod.getMCReturnType().getMCType());
    assertTrue(cdMethod.getModifier().isPublic());
    assertTrue(cdMethod.getModifier().isPresentStereotype());
    assertEquals(1, cdMethod.getModifier().getStereotype().sizeValue());
    assertEquals("methodBody", cdMethod.getModifier().getStereotype().getValue(0).getName());

    ASTMCObjectType cdInterface = scopeClass.getInterface(0);
    assertDeepEquals("de.monticore.symboltable.IScope", cdInterface);

    ASTMCObjectType superclass = scopeClass.getSuperclass();
    assertDeepEquals("de.monticore.mcbasics._symboltable.IMCBasicsScope", superclass);
  }

}
