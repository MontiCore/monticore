package de.monticore.codegen.cd2java._ast.ast_constants;

import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.constants.ASTConstantsDecorator;
import de.monticore.codegen.cd2java.factories.CDModifier;
import de.monticore.codegen.cd2java.factories.CDTypeFacade;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertInt;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getAttributeBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static org.junit.Assert.*;

public class ASTConstantsDecoratorTest extends DecoratorTestCase {

  private ASTCDClass constantClass;

  private GlobalExtensionManagement glex;

  private CDTypeFacade cdTypeFacade;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  @Before
  public void setUp() {
    this.cdTypeFacade = CDTypeFacade.getInstance();
    this.glex = new GlobalExtensionManagement();

    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));


    ASTConstantsDecorator decorator = new ASTConstantsDecorator(this.glex, new AbstractService(decoratedCompilationUnit));
    this.constantClass = decorator.decorate(decoratedCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testClassName() {
    assertEquals("ASTConstantsAutomaton", constantClass.getName());
  }

  @Test
  public void testAttributeCount() {
    assertEquals(5, constantClass.sizeCDAttributes());
  }

  @Test
  public void testLANGUAGEAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("LANGUAGE", constantClass);
    assertDeepEquals(CDModifier.PUBLIC_STATIC_FINAL, astcdAttribute.getModifier());
    assertDeepEquals("String", astcdAttribute.getType());
  }

  @Test
  public void testDEFAULTAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("DEFAULT", constantClass);
    assertDeepEquals(CDModifier.PUBLIC_STATIC_FINAL, astcdAttribute.getModifier());
    assertInt(astcdAttribute.getType());
  }

  @Test
  public void testFINALAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("FINAL", constantClass);
    assertDeepEquals(CDModifier.PUBLIC_STATIC_FINAL, astcdAttribute.getModifier());
    assertInt(astcdAttribute.getType());
  }

  @Test
  public void testINITIALAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("INITIAL", constantClass);
    assertDeepEquals(CDModifier.PUBLIC_STATIC_FINAL, astcdAttribute.getModifier());
    assertInt(astcdAttribute.getType());
  }

  @Test
  public void testSuperGrammarsAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("superGrammars", constantClass);
    assertDeepEquals(CDModifier.PUBLIC_STATIC, astcdAttribute.getModifier());
    assertDeepEquals(cdTypeFacade.createTypeByDefinition("String[]"), astcdAttribute.getType());
    assertFalse(astcdAttribute.isPresentValue());
  }

  @Test
  public void testMethodCount() {
    assertEquals(1, constantClass.sizeCDMethods());
  }

  @Test
  public void testGetAllLanguagesMethod() {
    ASTCDMethod method = getMethodBy("getAllLanguages", constantClass);
    assertDeepEquals(CDModifier.PUBLIC_STATIC, method.getModifier());
    assertDeepEquals(cdTypeFacade.createTypeByDefinition("Collection<String>"), method.getReturnType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, constantClass, constantClass);
    System.out.println(sb.toString());
  }


}
