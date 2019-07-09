package de.monticore.codegen.cd2java._ast.mill;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java.factories.CDTypeFacade;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertVoid;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MillDecoratorTest extends DecoratorTestCase {

  private CDTypeFacade cdTypeFacade;

  private ASTCDClass millClass;

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit originalCompilationUnit;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  @Before
  public void setUp() {
    Log.init();
    Log.enableFailQuick(false);
    this.glex = new GlobalExtensionManagement();
    this.cdTypeFacade = CDTypeFacade.getInstance();

    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));

    MillDecorator decorator = new MillDecorator(this.glex, new ASTService(decoratedCompilationUnit));
    this.millClass = decorator.decorate(decoratedCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testMillName() {
    assertEquals("AutomatonMill", millClass.getName());
  }

  @Test
  public void testAttributeName() {
    assertEquals("mill", millClass.getCDAttribute(0).getName());
    assertEquals("millASTAutomaton", millClass.getCDAttribute(1).getName());
    assertEquals("millASTState", millClass.getCDAttribute(2).getName());
    assertEquals("millASTTransition", millClass.getCDAttribute(3).getName());
  }

  @Test
  public void testAttributeModifier() {
    for (ASTCDAttribute astcdAttribute : millClass.getCDAttributeList()) {
      assertTrue(astcdAttribute.isPresentModifier());
      assertTrue(PROTECTED_STATIC.build().deepEquals(astcdAttribute.getModifier()));
    }
  }

  @Test
  public void testConstructor() {
    assertEquals(1, millClass.sizeCDConstructors());
    assertTrue(PROTECTED.build().deepEquals(millClass.getCDConstructor(0).getModifier()));
    assertEquals("AutomatonMill", millClass.getCDConstructor(0).getName());
  }

  @Test
  public void testGetMillMethod() {
    ASTCDMethod getMill = millClass.getCDMethod(0);
    //test Method Name
    assertEquals("getMill", getMill.getName());
    //test Parameters
    assertTrue(getMill.isEmptyCDParameters());
    //test ReturnType
    ASTMCType returnType = cdTypeFacade.createTypeByDefinition("AutomatonMill");
    assertTrue(getMill.getMCReturnType().isPresentMCType());
    assertDeepEquals(returnType, getMill.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PROTECTED_STATIC.build().deepEquals(getMill.getModifier()));
  }

  @Test
  public void testInitMeMethod() {
    ASTCDMethod initMe = millClass.getCDMethod(1);
    //test Method Name
    assertEquals("initMe", initMe.getName());
    //test Parameters
    assertEquals(1, initMe.sizeCDParameters());
    ASTMCType type = cdTypeFacade.createQualifiedType("AutomatonMill");
    assertDeepEquals(type, initMe.getCDParameter(0).getMCType());
    assertEquals("a", initMe.getCDParameter(0).getName());
    //test ReturnType
    assertTrue(initMe.getMCReturnType().isPresentMCVoidType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(initMe.getModifier()));
  }

  @Test
  public void testInitMethod() {
    ASTCDMethod init = millClass.getCDMethod(2);
    //test Method Name
    assertEquals("init", init.getName());
    //test Parameters
    assertTrue(init.isEmptyCDParameters());
    //test ReturnType
    assertTrue(init.getMCReturnType().isPresentMCVoidType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(init.getModifier()));
  }

  @Test
  public void testResetMethod() {
    ASTCDMethod reset = millClass.getCDMethod(3);
    //test Method Name
    assertEquals("reset", reset.getName());
    //test Parameters
    assertTrue(reset.isEmptyCDParameters());
    //test ReturnType
    assertTrue(reset.getMCReturnType().isPresentMCVoidType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(reset.getModifier()));
  }

  @Test
  public void testAutomatonBuilderMethod() {
    ASTCDMethod fooBarBuilder = millClass.getCDMethod(4);
    //test Method Name
    assertEquals("automatonBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    ASTMCType returnType = cdTypeFacade.createTypeByDefinition("ASTAutomatonBuilder");
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals(returnType, fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testProtectedAutomatonMethod() {
    ASTCDMethod fooBarBuilder = millClass.getCDMethod(5);
    //test Method Name
    assertEquals("_automatonBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    ASTMCType returnType = cdTypeFacade.createTypeByDefinition("ASTAutomatonBuilder");
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals(returnType, fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PROTECTED.build().deepEquals(fooBarBuilder.getModifier()));
  }


  @Test
  public void testStateMethod() {
    ASTCDMethod fooBarBuilder = millClass.getCDMethod(6);
    //test Method Name
    assertEquals("stateBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    ASTMCType returnType = cdTypeFacade.createTypeByDefinition("ASTStateBuilder");
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals(returnType, fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testProtectedStateBuilderMethod() {
    ASTCDMethod fooBarBuilder = millClass.getCDMethod(7);
    //test Method Name
    assertEquals("_stateBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    ASTMCType returnType = cdTypeFacade.createTypeByDefinition("ASTStateBuilder");
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals(returnType, fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PROTECTED.build().deepEquals(fooBarBuilder.getModifier()));
  }


  @Test
  public void testTransitionAMethod() {
    ASTCDMethod fooBarBuilder = millClass.getCDMethod(8);
    //test Method Name
    assertEquals("transitionBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    ASTMCType returnType = cdTypeFacade.createTypeByDefinition("ASTTransitionBuilder");
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals(returnType, fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testProtectedTransitionBuilderMethod() {
    ASTCDMethod fooBarBuilder = millClass.getCDMethod(9);
    //test Method Name
    assertEquals("_transitionBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    ASTMCType returnType = cdTypeFacade.createTypeByDefinition("ASTTransitionBuilder");
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals(returnType, fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PROTECTED.build().deepEquals(fooBarBuilder.getModifier()));
  }


  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, millClass, millClass);
    // TODO Check System.out.println(sb.toString());
  }
}
