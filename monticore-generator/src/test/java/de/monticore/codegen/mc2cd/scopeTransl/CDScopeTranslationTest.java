/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.mc2cd.scopeTransl;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.CDModifier;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.grammar.grammarfamily.GrammarFamilyMill;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
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

  @Before
  public void setup(){
    GrammarFamilyMill.init();
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Before
  public void setUp() {
    compilationUnit = TestHelper.parseAndTransformForScope(Paths
        .get("src/test/resources/mc2cdtransformation/scopeTransl/ScopeRule.mc4")).get();
  }

  @Test
  public void testDefinitionName() {
    assertEquals("ScopeRuleScope", compilationUnit.getCDDefinition().getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testPackage() {
    assertEquals(2, compilationUnit.getCDPackageList().size());
    assertEquals("mc2cdtransformation", compilationUnit.getMCPackageDeclaration().getMCQualifiedName().getParts(0));
    assertEquals("scopeTransl", compilationUnit.getMCPackageDeclaration().getMCQualifiedName().getParts(1));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testClassCount() {
    assertEquals(1, compilationUnit.getCDDefinition().getCDClassesList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testClassSymbol() {
    ASTCDClass scopeClass = getClassBy("ScopeRule", compilationUnit);
    assertEquals(1, scopeClass.getCDAttributeList().size());
    assertEquals(1, scopeClass.getCDMethodList().size());
    assertEquals(1, scopeClass.getInterfaceList().size());

    assertTrue(scopeClass.getCDConstructorList().isEmpty());
    assertTrue(scopeClass.isPresentCDExtendUsage());

    ASTCDAttribute cdAttribute = scopeClass.getCDAttributeList().get(0);
    assertEquals("extraAttr", cdAttribute.getName());
    assertDeepEquals(String.class, cdAttribute.getMCType());
    assertDeepEquals(CDModifier.PROTECTED, cdAttribute.getModifier());

    ASTCDMethod cdMethod = scopeClass.getCDMethodList().get(0);
    assertEquals("toString", cdMethod.getName());
    assertTrue(cdMethod.getMCReturnType().isPresentMCType());
    assertDeepEquals(String.class, cdMethod.getMCReturnType().getMCType());
    assertTrue(cdMethod.getModifier().isPublic());
    assertTrue(cdMethod.getModifier().isPresentStereotype());
    assertEquals(1, cdMethod.getModifier().getStereotype().sizeValues());
    assertEquals("methodBody", cdMethod.getModifier().getStereotype().getValues(0).getName());

    ASTMCObjectType cdInterface = scopeClass.getCDInterfaceUsage().getInterface(0);
    assertDeepEquals("de.monticore.symboltable.IScope", cdInterface);

    ASTMCObjectType superclass = scopeClass.getCDExtendUsage().getSuperclass(0);
    assertDeepEquals("de.monticore.mcbasics._symboltable.IMCBasicsScope", superclass);
  
    assertTrue(Log.getFindings().isEmpty());
  }

}
