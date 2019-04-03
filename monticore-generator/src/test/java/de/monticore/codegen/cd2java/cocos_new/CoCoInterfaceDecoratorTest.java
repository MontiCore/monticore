package de.monticore.codegen.cd2java.cocos_new;

import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java.ast_new.ASTService;
import de.monticore.codegen.cd2java.builder.BuilderDecorator;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.monticore.codegen.cd2java.DecoratorAssert.*;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.*;
import static de.monticore.codegen.cd2java.builder.BuilderDecorator.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

public class CoCoInterfaceDecoratorTest extends DecoratorTestCase {

  private final GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private List<ASTCDInterface> interfaces;

  @Before
  public void setup() {
    LogStub.init();
    ASTCDCompilationUnit ast = parse("de", "monticore", "codegen", "cocos", "CoCos");
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
    assertVoid(method.getReturnType());
    assertEquals("check", method.getName());
    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals("de.monticore.codegen.cocos.cocos._ast.ASTCoCosNode", parameter.getType());
    assertEquals("node", parameter.getName());
  }

  @Test
  public void testAInterface() {
    ASTCDInterface cdInterface = interfaces.get(1);
    assertEquals("CoCosASTACoCo", cdInterface.getName());
    assertEquals(1, cdInterface.getCDMethodList().size());
    ASTCDMethod method = cdInterface.getCDMethod(0);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertVoid(method.getReturnType());
    assertEquals("check", method.getName());
    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals("de.monticore.codegen.cocos.cocos._ast.ASTA", parameter.getType());
    assertEquals("node", parameter.getName());
  }

  @Test
  public void testIInterface() {
    ASTCDInterface cdInterface = interfaces.get(2);
    assertEquals("CoCosASTICoCo", cdInterface.getName());
    assertEquals(1, cdInterface.getCDMethodList().size());
    ASTCDMethod method = cdInterface.getCDMethod(0);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertVoid(method.getReturnType());
    assertEquals("check", method.getName());
    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals("de.monticore.codegen.cocos.cocos._ast.ASTI", parameter.getType());
    assertEquals("node", parameter.getName());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    for (ASTCDInterface i : interfaces) {
      System.out.printf("==================== %s ====================\n", i.getName());
      StringBuilder sb = generatorEngine.generate(CoreTemplates.INTERFACE, i, i);
      System.out.println(sb.toString());
    }
  }
}
