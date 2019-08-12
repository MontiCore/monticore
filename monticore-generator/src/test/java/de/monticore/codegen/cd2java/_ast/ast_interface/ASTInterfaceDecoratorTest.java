/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.ast_interface;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.ASTScopeDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java._ast.ast_class.ASTSymbolDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
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

    ASTCDCompilationUnit astcdCompilationUnit = this.parse("de", "monticore", "codegen", "data", "DataInterface");
    this.glex.setGlobalValue("service", new AbstractService(astcdCompilationUnit));
    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    SymbolTableService symbolTableService = new SymbolTableService(astcdCompilationUnit);
    ASTCDInterface interfaceBy = getInterfaceBy("ASTA", astcdCompilationUnit);
    ASTInterfaceDecorator decorator = new ASTInterfaceDecorator(this.glex, new ASTService(astcdCompilationUnit)
        , new VisitorService(astcdCompilationUnit), new ASTSymbolDecorator(this.glex, symbolTableService),
        new ASTScopeDecorator(this.glex, symbolTableService), new MethodDecorator(this.glex));
    ASTCDInterface changeInterface = CD4AnalysisMill.cDInterfaceBuilder().setName(interfaceBy.getName())
        .setModifier(interfaceBy.getModifier())
        .build();
    this.dataInterface = decorator.decorate(interfaceBy, changeInterface);
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
    assertEquals(3, dataInterface.sizeCDMethods());
  }

  @Test
  public void testAcceptMethod(){
    ASTCDMethod method = getMethodBy("accept",  dataInterface);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertFalse(method.isEmptyCDParameters());
    assertEquals(1, method.sizeCDParameters());

    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals("de.monticore.codegen.data.datainterface._visitor.DataInterfaceVisitor", parameter.getMCType());
    assertEquals("visitor", parameter.getName());
  }

  @Test
  public void testSuperInterfacesCount(){
    assertEquals(2, dataInterface.sizeInterfaces());
  }

  @Test
  public void testASTNodeSuperInterface(){
    ASTMCObjectType superInteface = dataInterface.getInterface(0);
    assertDeepEquals("de.monticore.ast.ASTNode", superInteface);
  }

  @Test
  public void testASTDataInterfaceNodeSuperInterface(){
    ASTMCObjectType superInteface = dataInterface.getInterface(1);
    assertDeepEquals("de.monticore.codegen.data.datainterface._ast.ASTDataInterfaceNode", superInteface);
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.INTERFACE, dataInterface, dataInterface);
    // TODO Check System.out.println(sb.toString());
  }

}
