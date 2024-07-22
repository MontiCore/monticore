/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.mc2cd.scopeTransl;

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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CDScopeTranslationTest extends TranslationTestCase {

  private ASTCDCompilationUnit compilationUnit;

  @BeforeEach
  public void setUp() {
    compilationUnit = TestHelper.parseAndTransformForScope(Paths
        .get("src/test/resources/mc2cdtransformation/scopeTransl/ScopeRule.mc4")).get();
  }

  @Test
  public void testDefinitionName() {
    Assertions.assertEquals("ScopeRuleScope", compilationUnit.getCDDefinition().getName());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testPackage() {
    Assertions.assertEquals(2, compilationUnit.getCDPackageList().size());
    Assertions.assertEquals("mc2cdtransformation", compilationUnit.getMCPackageDeclaration().getMCQualifiedName().getParts(0));
    Assertions.assertEquals("scopeTransl", compilationUnit.getMCPackageDeclaration().getMCQualifiedName().getParts(1));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testClassCount() {
    Assertions.assertEquals(1, compilationUnit.getCDDefinition().getCDClassesList().size());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testClassSymbol() {
    ASTCDClass scopeClass = getClassBy("ScopeRule", compilationUnit);
    Assertions.assertEquals(1, scopeClass.getCDAttributeList().size());
    Assertions.assertEquals(1, scopeClass.getCDMethodList().size());
    Assertions.assertEquals(1, scopeClass.getInterfaceList().size());

    Assertions.assertTrue(scopeClass.getCDConstructorList().isEmpty());
    Assertions.assertTrue(scopeClass.isPresentCDExtendUsage());

    ASTCDAttribute cdAttribute = scopeClass.getCDAttributeList().get(0);
    Assertions.assertEquals("extraAttr", cdAttribute.getName());
    assertDeepEquals(String.class, cdAttribute.getMCType());
    assertDeepEquals(CDModifier.PROTECTED, cdAttribute.getModifier());

    ASTCDMethod cdMethod = scopeClass.getCDMethodList().get(0);
    Assertions.assertEquals("toString", cdMethod.getName());
    Assertions.assertTrue(cdMethod.getMCReturnType().isPresentMCType());
    assertDeepEquals(String.class, cdMethod.getMCReturnType().getMCType());
    Assertions.assertTrue(cdMethod.getModifier().isPublic());
    Assertions.assertTrue(cdMethod.getModifier().isPresentStereotype());
    Assertions.assertEquals(1, cdMethod.getModifier().getStereotype().sizeValues());
    Assertions.assertEquals("methodBody", cdMethod.getModifier().getStereotype().getValues(0).getName());

    ASTMCObjectType cdInterface = scopeClass.getCDInterfaceUsage().getInterface(0);
    assertDeepEquals("de.monticore.symboltable.IScope", cdInterface);

    ASTMCObjectType superclass = scopeClass.getCDExtendUsage().getSuperclass(0);
    assertDeepEquals("de.monticore.mcbasics._symboltable.IMCBasicsScope", superclass);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}
