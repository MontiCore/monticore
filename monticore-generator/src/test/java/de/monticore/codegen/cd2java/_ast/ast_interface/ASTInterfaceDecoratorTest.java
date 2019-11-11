/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.ast_interface;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
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
import org.mockito.Mockito;

import static de.monticore.codegen.cd2java.DecoratorAssert.*;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getInterfaceBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC_ABSTRACT;
import static org.junit.Assert.*;

public class ASTInterfaceDecoratorTest extends DecoratorTestCase {

  private ASTCDInterface dataInterface;

  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

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
  public void testMethodCount() {
    assertEquals(7, dataInterface.sizeCDMethods());
  }

  @Test
  public void testAcceptMethod() {
    ASTCDMethod method = getMethodBy("accept", dataInterface);
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
    ASTCDMethod method = getMethodBy("getSymbol", dataInterface);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.data.datainterface._symboltable.ASymbol", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetSymbolOptMethod() {
    ASTCDMethod method = getMethodBy("getSymbolOpt", dataInterface);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertOptionalOf("de.monticore.codegen.data.datainterface._symboltable.ASymbol", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testIsPresentSymbolMethod() {
    ASTCDMethod method = getMethodBy("isPresentSymbol", dataInterface);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }


  @Test
  public void testGetEnclosingScopeMethod() {
    ASTCDMethod method = getMethodBy("getEnclosingScope", dataInterface);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.data.datainterface._symboltable.IDataInterfaceScope",
        method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSetEnclosingScopeMethod() {
    ASTCDMethod method = getMethodBy("setEnclosingScope", dataInterface);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());

    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals("de.monticore.codegen.data.datainterface._symboltable.IDataInterfaceScope",
        parameter.getMCType());
    assertEquals("enclosingScope", parameter.getName());
  }


  @Test
  public void testGetNameMethod() {
    ASTCDMethod method = getMethodBy("getName", dataInterface);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSuperInterfacesCount() {
    assertEquals(2, dataInterface.sizeInterfaces());
  }

  @Test
  public void testASTNodeSuperInterface() {
    ASTMCObjectType superInteface = dataInterface.getInterface(0);
    assertDeepEquals("de.monticore.ast.ASTNode", superInteface);
  }

  @Test
  public void testASTDataInterfaceNodeSuperInterface() {
    ASTMCObjectType superInteface = dataInterface.getInterface(1);
    assertDeepEquals("de.monticore.codegen.data.datainterface._ast.ASTDataInterfaceNode", superInteface);
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.INTERFACE, dataInterface, dataInterface);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  }

  @Test
  public void testSymbolInterfaceWithNoName() {
    GlobalExtensionManagement glex = new GlobalExtensionManagement();

    ASTCDCompilationUnit astcdCompilationUnit = this.parse("de", "monticore", "codegen", "data", "DataInterface");
    glex.setGlobalValue("service", new AbstractService(astcdCompilationUnit));
    glex.setGlobalValue("astHelper", new DecorationHelper());
    SymbolTableService symbolTableService = new SymbolTableService(astcdCompilationUnit);
    ASTService mockService = Mockito.spy(new ASTService(astcdCompilationUnit));
    ASTCDInterface interfaceBy = getInterfaceBy("ASTA", astcdCompilationUnit);
    ASTInterfaceDecorator decorator = new ASTInterfaceDecorator(glex, mockService
        , new VisitorService(astcdCompilationUnit), new ASTSymbolDecorator(glex, symbolTableService),
        new ASTScopeDecorator(glex, symbolTableService), new MethodDecorator(glex));
    ASTCDInterface changeInterface = CD4AnalysisMill.cDInterfaceBuilder().setName(interfaceBy.getName())
        .setModifier(interfaceBy.getModifier())
        .build();
    // return true, when asked if is symbol without name
    Mockito.doReturn(true).when(mockService).isSymbolWithoutName(Mockito.any(ASTCDType.class));

    ASTCDInterface noNameSymbol = decorator.decorate(interfaceBy, changeInterface);

    assertEquals(7, noNameSymbol.sizeCDMethods());

    ASTCDMethod method = getMethodBy("getName", noNameSymbol);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

}
