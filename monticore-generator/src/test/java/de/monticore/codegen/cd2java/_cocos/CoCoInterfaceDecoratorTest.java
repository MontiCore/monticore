package de.monticore.codegen.cd2java._cocos;

import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.cd.cd4analysis._ast.ASTCDParameter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertVoid;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC_ABSTRACT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CoCoInterfaceDecoratorTest extends DecoratorTestCase {

  private final GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private List<ASTCDInterface> interfaces;

  @Before
  public void setup() {
    LogStub.init();
    ASTCDCompilationUnit ast = parse("de", "monticore", "codegen", "cocos", "CoCos");
    this.glex.setGlobalValue("service", new AbstractService(ast));

    CoCoInterfaceDecorator coCoInterfaceDecorator = new CoCoInterfaceDecorator(glex, new CoCoService(ast), new ASTService(ast));
    this.interfaces = coCoInterfaceDecorator.decorate(ast.getCDDefinition());
  }

  @Test
  public void testInterfaceSize() {
    assertEquals(3, interfaces.size());
  }

  @Test
  public void testCoCosNodeInterface() {
    ASTCDInterface cdInterface = interfaces.get(0);
    assertEquals("CoCosASTCoCosNodeCoCo", cdInterface.getName());
    assertEquals(1, cdInterface.getCDMethodList().size());
    ASTCDMethod method = cdInterface.getCDMethod(0);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals("check", method.getName());
    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals("de.monticore.codegen.cocos.cocos._ast.ASTCoCosNode", parameter.getMCType());
    assertEquals("node", parameter.getName());
  }

  @Test
  public void testAInterface() {
    ASTCDInterface cdInterface = interfaces.get(1);
    assertEquals("CoCosASTACoCo", cdInterface.getName());
    assertEquals(1, cdInterface.getCDMethodList().size());
    ASTCDMethod method = cdInterface.getCDMethod(0);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals("check", method.getName());
    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals("de.monticore.codegen.cocos.cocos._ast.ASTA", parameter.getMCType());
    assertEquals("node", parameter.getName());
  }

  @Test
  public void testIInterface() {
    ASTCDInterface cdInterface = interfaces.get(2);
    assertEquals("CoCosASTICoCo", cdInterface.getName());
    assertEquals(1, cdInterface.getCDMethodList().size());
    ASTCDMethod method = cdInterface.getCDMethod(0);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals("check", method.getName());
    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals("de.monticore.codegen.cocos.cocos._ast.ASTI", parameter.getMCType());
    assertEquals("node", parameter.getName());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    for (ASTCDInterface i : interfaces) {
      // System.out.printf("==================== %s ====================\n", i.getName());
      StringBuilder sb = generatorEngine.generate(CoreTemplates.INTERFACE, i, i);
      // TODO: Check System.out.println(sb.toString());
    }
  }
}
