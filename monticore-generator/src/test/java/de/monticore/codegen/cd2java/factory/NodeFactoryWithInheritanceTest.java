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
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;
import static org.junit.Assert.*;

public class NodeFactoryWithInheritanceTest extends DecoratorTestCase {

  private CDTypeFacade cdTypeFacade;

  private CDParameterFacade cdParameterFacade;

  private ASTCDClass factoryClass;

  private GlobalExtensionManagement glex;

  @Before
  public void setUp() {
    this.glex = new GlobalExtensionManagement();
    this.cdTypeFacade = CDTypeFacade.getInstance();
    this.cdParameterFacade = CDParameterFacade.getInstance();

    ASTCDCompilationUnit compilationUnit = this.parse("de", "monticore", "codegen", "factory", "CGrammar");
    this.glex.setGlobalValue("service", new AbstractService(compilationUnit));
    this.glex.setGlobalValue("astHelper", new DecorationHelper());

    NodeFactoryDecorator decorator = new NodeFactoryDecorator(this.glex, new NodeFactoryService(compilationUnit));
    this.factoryClass = decorator.decorate(compilationUnit);
  }

  @Test
  public void testFactoryName() {
    assertEquals("CGrammarNodeFactory", factoryClass.getName());
  }

  @Test
  public void testAttributeName() {
    assertEquals(3, factoryClass.sizeCDAttributes());
    assertEquals("factory", factoryClass.getCDAttribute(0).getName());
    assertEquals("factoryASTBlub", factoryClass.getCDAttribute(1).getName());
    assertEquals("factoryASTBli", factoryClass.getCDAttribute(2).getName());
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
        .setName("CGrammarNodeFactory")
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
    ASTType returnType = cdTypeFacade.createTypeByDefinition("CGrammarNodeFactory");
    assertDeepEquals(returnType, method.getReturnType());
  }

  @Test
  public void testMethodCreateDelegateASTC() {
    ASTCDMethod method = factoryClass.getCDMethod(9);
    //test name
    assertEquals("createASTB", method.getName());
    //test modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(method.getModifier()));
    //test parameters
    assertTrue(method.isEmptyCDParameters());
    //test returnType
    ASTType returnType = cdTypeFacade.createTypeByDefinition("de.monticore.codegen.factory.bgrammar._ast.ASTB");
    assertDeepEquals(returnType, method.getReturnType());
  }

  @Test
  public void testMethodCreateDelegateASTFoo() {
    ASTCDMethod method = factoryClass.getCDMethod(10);
    //test name
    assertEquals("createASTFoo", method.getName());
    //test modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(method.getModifier()));
    //test parameters
    assertTrue(method.isEmptyCDParameters());
    //test returnType
    ASTType returnType = cdTypeFacade.createTypeByDefinition("de.monticore.codegen.factory.agrammar._ast.ASTFoo");
    assertDeepEquals(returnType, method.getReturnType());
  }

  @Test
  public void testMethodCreateDelegateASTBar() {
    ASTCDMethod method = factoryClass.getCDMethod(12);
    //test name
    assertEquals("createASTBar", method.getName());
    //test modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(method.getModifier()));
    //test parameters
    assertTrue(method.isEmptyCDParameters());
    //test returnType
    ASTType returnType = cdTypeFacade.createTypeByDefinition("de.monticore.codegen.factory.agrammar._ast.ASTBar");
    assertDeepEquals(returnType, method.getReturnType());
  }

  @Test
  public void testMethodCreateDelegateASTFooWithParam() {
    ASTCDMethod method = factoryClass.getCDMethod(11);
    //test name
    assertEquals("createASTFoo", method.getName());
    //test modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(method.getModifier()));
    //test parameters
    assertFalse(method.isEmptyCDParameters());
    assertEquals(1, method.sizeCDParameters());

    //test returnType
    ASTType returnType = cdTypeFacade.createTypeByDefinition("de.monticore.codegen.factory.agrammar._ast.ASTFoo");
    assertDeepEquals(returnType, method.getReturnType());
  }

  @Test
  public void testMethodCreateDelegateASTBarWithParam() {
    ASTCDMethod method = factoryClass.getCDMethod(13);
    //test name
    assertEquals("createASTBar", method.getName());
    //test modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(method.getModifier()));
    //test parameters
    assertFalse(method.isEmptyCDParameters());
    assertEquals(2, method.sizeCDParameters());

    ASTType fooType = cdTypeFacade.createSimpleReferenceType("de.monticore.codegen.factory.agrammar._ast.ASTFoo");
    ASTCDParameter fooParameter = cdParameterFacade.createParameter(fooType, "foo");
    //assertTrue(fooParameter.getType().deepEquals(method.getCDParameter(0).getType())); todo fix ast->cd transformation
    assertEquals("de.monticore.codegen.factory.agrammar._ast.ASTFoo", TypesPrinter.printType(method.getCDParameter(0).getType()));
    assertEquals(fooParameter.getName(), method.getCDParameter(0).getName());
    //test returnType
    ASTType returnType = cdTypeFacade.createTypeByDefinition("de.monticore.codegen.factory.agrammar._ast.ASTBar");
    assertDeepEquals(returnType, method.getReturnType());
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
