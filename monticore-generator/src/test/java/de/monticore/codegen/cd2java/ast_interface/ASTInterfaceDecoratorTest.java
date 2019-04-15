package de.monticore.codegen.cd2java.ast_interface;

import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java.ast_new.ASTService;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.visitor_new.VisitorService;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTReferenceType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDParameter;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertVoid;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getInterfaceBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC_ABSTRACT;
import static org.junit.Assert.*;

public class ASTInterfaceDecoratorTest extends DecoratorTestCase {

  private ASTCDInterface dataInterface;

  private GlobalExtensionManagement glex;

  @Before
  public void setUp() {
    this.glex = new GlobalExtensionManagement();

    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    ASTCDCompilationUnit compilationUnit = this.parse("de", "monticore", "codegen", "data", "DataInterface");
    ASTCDInterface astcdInterface = getInterfaceBy("ASTA", compilationUnit);
    ASTInterfaceDecorator decorator = new ASTInterfaceDecorator(this.glex, new ASTService(compilationUnit), new VisitorService(compilationUnit));
    this.dataInterface = decorator.decorate(astcdInterface);
  }

  @Test
  public void testClassSignature() {
    assertEquals("ASTA", dataInterface.getName());
  }

  @Test
  public void testAttributesCount() {
    assertTrue(dataInterface.isEmptyCDAttributes());
  }

  @Test
  public void testMethodCount(){
    assertEquals(1, dataInterface.sizeCDMethods());
  }

  @Test
  public void testAcceptMethod(){
    ASTCDMethod method = getMethodBy("accept",  dataInterface);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertVoid(method.getReturnType());

    assertFalse(method.isEmptyCDParameters());
    assertEquals(1, method.sizeCDParameters());

    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals("de.monticore.codegen.data.datainterface._visitor.DataInterfaceVisitor", parameter.getType());
    assertEquals("visitor", parameter.getName());
  }

  @Test
  public void testSuperInterfacesCount(){
    assertEquals(2, dataInterface.sizeInterfaces());
  }

  @Test
  public void testASTNodeSuperInterface(){
    ASTReferenceType superInteface = dataInterface.getInterface(0);
    assertDeepEquals("de.monticore.ast.ASTNode", superInteface);
  }

  @Test
  public void testASTDataInterfaceNodeSuperInterface(){
    ASTReferenceType superInteface = dataInterface.getInterface(1);
    assertDeepEquals("de.monticore.codegen.data.datainterface._ast.ASTDataInterfaceNode", superInteface);
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.INTERFACE, dataInterface, dataInterface);
    System.out.println(sb.toString());
  }

}
