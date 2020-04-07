/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.ast_interface;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.ASTScopeDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java._ast.ast_class.ASTSymbolDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.reference.ASTReferenceDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.data.DataDecoratorUtil;
import de.monticore.codegen.cd2java.data.InterfaceDecorator;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.cd.facade.CDModifier.PUBLIC_ABSTRACT;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertBoolean;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getInterfaceBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static org.junit.Assert.*;

public class FullASTInterfaceDecoratorTest extends DecoratorTestCase {

  private ASTCDInterface astcdInterface;

  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private static final String NAME_DEFINITION = "de.monticore.codegen.ast.referencedsymbol._ast.ASTFoo";

  private static final String NAME_SYMBOL = "de.monticore.codegen.ast.referencedsymbol._symboltable.FooSymbol";

  @Before
  public void setUp() {
    this.glex = new GlobalExtensionManagement();

    ASTCDCompilationUnit astcdCompilationUnit = this.parse("de", "monticore", "codegen", "data", "DataInterface");
    this.glex.setGlobalValue("service", new AbstractService(astcdCompilationUnit));
    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());

    SymbolTableService symbolTableService = new SymbolTableService(astcdCompilationUnit);
    ASTCDInterface interfaceBy = getInterfaceBy("ASTA", astcdCompilationUnit);
    ASTInterfaceDecorator astInterfaceDecorator = new ASTInterfaceDecorator(this.glex, new ASTService(astcdCompilationUnit)
        , new VisitorService(astcdCompilationUnit), new ASTSymbolDecorator(this.glex, symbolTableService),
        new ASTScopeDecorator(this.glex, symbolTableService), new MethodDecorator(this.glex, symbolTableService));

    ASTService astService = new ASTService(astcdCompilationUnit);
    MethodDecorator methodDecorator = new MethodDecorator(glex, astService);
    InterfaceDecorator interfaceDecorator = new InterfaceDecorator(glex, new DataDecoratorUtil(), methodDecorator, astService);

    ASTReferenceDecorator<ASTCDInterface> referenceDecorator =
        new ASTReferenceDecorator<ASTCDInterface>(glex, symbolTableService);

    ASTCDInterface changeInterface = CD4AnalysisMill.cDInterfaceBuilder().setName(interfaceBy.getName())
        .setModifier(interfaceBy.getModifier())
        .build();

    FullASTInterfaceDecorator fullASTInterfaceDecorator = new FullASTInterfaceDecorator(interfaceDecorator,
        astInterfaceDecorator, referenceDecorator);
    this.astcdInterface = fullASTInterfaceDecorator.decorate(interfaceBy, changeInterface);
  }

  @Test
  public void testClassSignature() {
    assertEquals("ASTA", astcdInterface.getName());
  }

  @Test
  public void testSuperInterfacesCount() {
    assertEquals(2, astcdInterface.sizeInterfaces());
  }

  @Test
  public void testASTNodeSuperInterface() {
    ASTMCObjectType superInteface = astcdInterface.getInterface(0);
    assertDeepEquals("de.monticore.ast.ASTNode", superInteface);
  }

  @Test
  public void testASTDataInterfaceNodeSuperInterface() {
    ASTMCObjectType superInteface = astcdInterface.getInterface(1);
    assertDeepEquals("de.monticore.codegen.data.datainterface._ast.ASTDataInterfaceNode", superInteface);
  }

  @Test
  public void testAttributesCount() {
    assertTrue(astcdInterface.isEmptyCDAttributes());
  }

  @Test
  public void testMethodCount() {
    assertEquals(61, astcdInterface.sizeCDMethods());
  }

  /**
   * referenced symbol methods
   */

  @Test
  public void testGetNameDefinitionMethod() {
    ASTCDMethod method = getMethodBy("getFooDefinition", astcdInterface);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(NAME_DEFINITION, method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testIsPresentFooDefinitionMethod() {
    ASTCDMethod method = getMethodBy("isPresentFooDefinition", astcdInterface);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetFooSymbolMethod() {
    ASTCDMethod method = getMethodBy("getFooSymbol", astcdInterface);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(NAME_SYMBOL, method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testIsPresentFooSymbolMethod() {
    ASTCDMethod method = getMethodBy("isPresentFooSymbol", astcdInterface);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }


  /**
   * ASTInterface methods
   */
  @Test
  public void testAcceptMethod() {
    ASTCDMethod method = getMethodBy("accept", astcdInterface);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertFalse(method.isEmptyCDParameters());
    assertEquals(1, method.sizeCDParameters());

    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals("de.monticore.codegen.data.datainterface._visitor.DataInterfaceVisitor", parameter.getMCType());
    assertEquals("visitor", parameter.getName());
  }

  @Test
  public void testGetSymbolMethod() {
    ASTCDMethod method = getMethodBy("getSymbol", astcdInterface);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.data.datainterface._symboltable.ASymbol", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testIsPresentSymbolMethod() {
    ASTCDMethod method = getMethodBy("isPresentSymbol", astcdInterface);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }


  @Test
  public void testGetEnclosingScopeMethod() {
    ASTCDMethod method = getMethodBy("getEnclosingScope", astcdInterface);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.data.datainterface._symboltable.IDataInterfaceScope",
        method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSetEnclosingScopeMethod() {
    ASTCDMethod method = getMethodBy("setEnclosingScope", astcdInterface);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());

    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals("de.monticore.codegen.data.datainterface._symboltable.IDataInterfaceScope",
        parameter.getMCType());
    assertEquals("enclosingScope", parameter.getName());
  }


  @Test
  public void testGetFooMethod() {
    ASTCDMethod method = getMethodBy("getFoo", astcdInterface);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }


  /**
   * DataInterface methods
   */
  @Test
  public void testDeepEquals() {
    ASTCDMethod method = getMethodBy("deepEquals", 1, astcdInterface);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());

    assertFalse(method.isEmptyCDParameters());
    assertEquals(1, method.sizeCDParameters());

    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(Object.class, parameter.getMCType());
    assertEquals("o", parameter.getName());
  }

  @Test
  public void testDeepEqualsForceSameOrder() {
    ASTCDMethod method = getMethodBy("deepEquals", 2, astcdInterface);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());

    assertFalse(method.isEmptyCDParameters());
    assertEquals(2, method.sizeCDParameters());

    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(Object.class, parameter.getMCType());
    assertEquals("o", parameter.getName());

    parameter = method.getCDParameter(1);
    assertBoolean(parameter.getMCType());
    assertEquals("forceSameOrder", parameter.getName());
  }

  @Test
  public void testDeepEqualsWithComments() {
    ASTCDMethod method = getMethodBy("deepEqualsWithComments", 1, astcdInterface);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());

    assertFalse(method.isEmptyCDParameters());
    assertEquals(1, method.sizeCDParameters());

    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(Object.class, parameter.getMCType());
    assertEquals("o", parameter.getName());
  }

  @Test
  public void testDeepEqualsWithCommentsForceSameOrder() {
    ASTCDMethod method = getMethodBy("deepEqualsWithComments", 2, astcdInterface);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());

    assertFalse(method.isEmptyCDParameters());
    assertEquals(2, method.sizeCDParameters());

    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(Object.class, parameter.getMCType());
    assertEquals("o", parameter.getName());

    parameter = method.getCDParameter(1);
    assertBoolean(parameter.getMCType());
    assertEquals("forceSameOrder", parameter.getName());
  }

  @Test
  public void testEqualAttributes() {
    ASTCDMethod method = getMethodBy("equalAttributes", astcdInterface);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());

    assertFalse(method.isEmptyCDParameters());
    assertEquals(1, method.sizeCDParameters());

    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(Object.class, parameter.getMCType());
    assertEquals("o", parameter.getName());
  }

  @Test
  public void testEqualsWithComments() {
    ASTCDMethod method = getMethodBy("equalsWithComments", astcdInterface);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());

    assertFalse(method.isEmptyCDParameters());
    assertEquals(1, method.sizeCDParameters());

    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(Object.class, parameter.getMCType());
    assertEquals("o", parameter.getName());
  }

  @Test
  public void testDeepClone() {
    ASTCDMethod method = getMethodBy("deepClone", 0, astcdInterface);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(astcdInterface.getName(), method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }


  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.INTERFACE, astcdInterface, astcdInterface);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  }


}
