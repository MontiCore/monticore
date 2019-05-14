package de.monticore.codegen.cd2java.factory;

import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java.factories.CDParameterFacade;
import de.monticore.codegen.cd2java.factories.CDTypeFacade;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;
import static org.junit.Assert.*;

public class NodeFactoryDecoratorTest extends DecoratorTestCase {

  private CDTypeFacade cdTypeFacade;

  private CDParameterFacade cdParameterFacade;

  private ASTCDClass factoryClass;

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  @Before
  public void setUp() {
    this.glex = new GlobalExtensionManagement();
    this.cdTypeFacade = CDTypeFacade.getInstance();
    this.cdParameterFacade = CDParameterFacade.getInstance();

    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));
    this.glex.setGlobalValue("astHelper", new DecorationHelper());

    NodeFactoryDecorator decorator = new NodeFactoryDecorator(this.glex, new NodeFactoryService(decoratedCompilationUnit));
    this.factoryClass = decorator.decorate(decoratedCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testFactoryName() {
    assertEquals("AutomatonNodeFactory", factoryClass.getName());
  }

  @Test
  public void testAttributeName() {
    assertEquals("factory", factoryClass.getCDAttribute(0).getName());
    assertEquals("factoryASTAutomaton", factoryClass.getCDAttribute(1).getName());
    assertEquals("factoryASTState", factoryClass.getCDAttribute(2).getName());
    assertEquals("factoryASTTransition", factoryClass.getCDAttribute(3).getName());
  }

  @Test
  public void testAttributeModifier() {
    for (ASTCDAttribute astcdAttribute : factoryClass.getCDAttributeList()) {
      assertTrue(astcdAttribute.isPresentModifier());
      assertTrue(PROTECTED_STATIC.build().deepEquals(astcdAttribute.getModifier()));
    }
  }

  @Test
  public void testConstructor() {
    assertEquals(1, factoryClass.sizeCDConstructors());
    ASTCDConstructor astcdConstructor = CD4AnalysisMill.cDConstructorBuilder()
        .setModifier(PROTECTED.build())
        .setName("AutomatonNodeFactory")
        .build();
    assertDeepEquals(astcdConstructor, factoryClass.getCDConstructor(0));
  }

  @Test
  public void testMethodGetFactory() {
    ASTCDMethod method = factoryClass.getCDMethod(0);
    //test name
    assertEquals("getFactory", method.getName());
    //test modifier
    assertTrue(PRIVATE_STATIC.build().deepEquals(method.getModifier()));
    //test parameters
    assertTrue(method.isEmptyCDParameters());
    //test returnType
    ASTType returnType = cdTypeFacade.createTypeByDefinition("AutomatonNodeFactory");
    assertDeepEquals(returnType, method.getReturnType());
  }

  @Test
  public void testMethodCreateASTAutomatonWithoutParameter() {
    ASTCDMethod method = factoryClass.getCDMethod(1);
    //test name
    assertEquals("createASTAutomaton", method.getName());
    //test modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(method.getModifier()));
    //test parameters
    assertTrue(method.isEmptyCDParameters());
    //test returnType
    ASTType returnType = cdTypeFacade.createTypeByDefinition("ASTAutomaton");
    assertDeepEquals(returnType, method.getReturnType());
  }

  @Test
  public void testMethodDoCreateASTAutomatonWithoutParameter() {
    ASTCDMethod method = factoryClass.getCDMethod(2);
    //test name
    assertEquals("doCreateASTAutomaton", method.getName());
    //test modifier
    assertTrue(PROTECTED.build().deepEquals(method.getModifier()));
    //test parameters
    assertTrue(method.isEmptyCDParameters());
    //test returnType
    ASTType returnType = cdTypeFacade.createTypeByDefinition("ASTAutomaton");
    assertDeepEquals(returnType, method.getReturnType());
  }

  @Test
  public void testMethodCreateASTAutomatonWithParameter() {
    ASTCDMethod method = factoryClass.getCDMethod(3);
    //test name
    assertEquals("createASTAutomaton", method.getName());
    //test modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(method.getModifier()));
    //test returnType
    ASTType returnType = cdTypeFacade.createTypeByDefinition("ASTAutomaton");
    assertDeepEquals(returnType, method.getReturnType());
    //testParameter
    assertFalse(method.isEmptyCDParameters());

    ASTType nameType = cdTypeFacade.createTypeByDefinition("String");
    ASTCDParameter nameParameter = cdParameterFacade.createParameter(nameType, "name");
    assertDeepEquals(nameParameter.getType(), method.getCDParameter(0).getType());
    assertEquals(nameParameter.getName(), method.getCDParameter(0).getName());

    ASTType statesType = cdTypeFacade.createTypeByDefinition("java.util.List<de.monticore.codegen.ast.automaton._ast.ASTState>");
    ASTCDParameter statesParameter = cdParameterFacade.createParameter(statesType, "states");
    assertDeepEquals(statesParameter.getType(), method.getCDParameter(1).getType());
    assertEquals(statesParameter.getName(), method.getCDParameter(1).getName());

    ASTType transitionsType = cdTypeFacade.createTypeByDefinition("java.util.List<de.monticore.codegen.ast.automaton._ast.ASTTransition>");
    ASTCDParameter transitionsParameter = cdParameterFacade.createParameter(transitionsType, "transitions");
    assertDeepEquals(transitionsParameter.getType(), method.getCDParameter(2).getType());
    assertEquals(transitionsParameter.getName(), method.getCDParameter(2).getName());
  }

  @Test
  public void testMethodDoCreateASTAutomatonWithParameter() {
    ASTCDMethod method = factoryClass.getCDMethod(4);
    //test name
    assertEquals("doCreateASTAutomaton", method.getName());
    //test modifier
    assertTrue(PROTECTED.build().deepEquals(method.getModifier()));
    //test returnType
    ASTType returnType = cdTypeFacade.createTypeByDefinition("ASTAutomaton");
    assertDeepEquals(returnType, method.getReturnType());
    //testParameter
    assertFalse(method.isEmptyCDParameters());

    ASTType nameType = cdTypeFacade.createTypeByDefinition("String");
    ASTCDParameter nameParameter = cdParameterFacade.createParameter(nameType, "name");
    assertDeepEquals(nameParameter.getType(), method.getCDParameter(0).getType());
    assertEquals(nameParameter.getName(), method.getCDParameter(0).getName());

    ASTType statesType = cdTypeFacade.createTypeByDefinition("java.util.List<automaton._ast.ASTState>");
    ASTCDParameter statesParameter = cdParameterFacade.createParameter(statesType, "states");
    assertDeepEquals(statesParameter.getType(), statesType);
    assertEquals(statesParameter.getName(), method.getCDParameter(1).getName());

    ASTType transitionsType = cdTypeFacade.createTypeByDefinition("java.util.List<automaton._ast.ASTTransition>");
    ASTCDParameter transitionsParameter = cdParameterFacade.createParameter(transitionsType, "transitions");
    assertDeepEquals(transitionsParameter.getType(), transitionsType);
    assertEquals(transitionsParameter.getName(), method.getCDParameter(2).getName());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, factoryClass, factoryClass);
    System.out.println(sb.toString());
  }
}
