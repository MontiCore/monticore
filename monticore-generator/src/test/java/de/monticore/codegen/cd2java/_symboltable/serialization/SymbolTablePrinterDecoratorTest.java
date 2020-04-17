/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.serialization;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.DecoratorAssert.*;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.*;
import static org.junit.Assert.*;

public class SymbolTablePrinterDecoratorTest extends DecoratorTestCase {

  private ASTCDClass symbolTablePrinter;

  private GlobalExtensionManagement glex;

  private MCTypeFacade mcTypeFacade;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  private static final String AUTOMATON_SYMBOL = "de.monticore.codegen.symboltable.automaton._symboltable.AutomatonSymbol";

  private static final String STATE_SYMBOL = "de.monticore.codegen.symboltable.automaton._symboltable.StateSymbol";

  private static final String I_AUTOMATON_SCOPE = "de.monticore.codegen.symboltable.automaton._symboltable.IAutomatonScope";

  private static final String AUTOMATON_SCOPE = "de.monticore.codegen.symboltable.automaton._symboltable.AutomatonScope";

  private static final String AUTOMATON_ARTIFACT_SCOPE = "de.monticore.codegen.symboltable.automaton._symboltable.AutomatonArtifactScope";

  private static final String I_SCOPE_SPANNING_SYMBOL = "de.monticore.symboltable.IScopeSpanningSymbol";

  @Before
  public void setUp() {
    Log.init();
    this.mcTypeFacade = MCTypeFacade.getInstance();
    this.glex = new GlobalExtensionManagement();

    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "Automaton");

    ASTCDCompilationUnit symbolCD = this.parse("de", "monticore", "codegen", "symboltable", "AutomatonSymbolCD");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));

    SymbolTablePrinterDecorator decorator = new SymbolTablePrinterDecorator(this.glex, new SymbolTableService(decoratedCompilationUnit),
        new VisitorService(decoratedCompilationUnit));
    this.symbolTablePrinter = decorator.decorate(decoratedCompilationUnit, symbolCD);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testClassNameAutomatonSymbol() {
    assertEquals("AutomatonSymbolTablePrinter", symbolTablePrinter.getName());
  }

  @Test
  public void testSuperInterfacesCountAutomatonSymbol() {
    assertEquals(1, symbolTablePrinter.sizeInterfaces());
  }

  @Test
  public void testSuperInterfacesAutomatonSymbol() {
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._visitor.AutomatonScopeVisitor", symbolTablePrinter.getInterface(0));
  }

  @Test
  public void testNoSuperClass() {
    assertFalse(symbolTablePrinter.isPresentSuperclass());
  }

  @Test
  public void testConstructor() {
    assertEquals(1, symbolTablePrinter.getCDConstructorList().size());
    ASTCDConstructor astcdConstructor = symbolTablePrinter.getCDConstructor(0);
    assertDeepEquals(PUBLIC, astcdConstructor.getModifier());
  }

  @Test
  public void testAttributesSize() {
    assertEquals(2, symbolTablePrinter.sizeCDAttributes());
  }

  @Test
  public void testJsonPrinterAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("printer", symbolTablePrinter);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals("de.monticore.symboltable.serialization.JsonPrinter", astcdAttribute.getMCType());
  }

  @Test
  public void testMethods() {
    assertEquals(44, symbolTablePrinter.getCDMethodList().size());
  }

  @Test
  public void testGetJsonPrinterMethod(){
    ASTCDMethod method = getMethodBy("getJsonPrinter", symbolTablePrinter);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.symboltable.serialization.JsonPrinter", method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSetJsonPrinterMethod(){
    ASTCDMethod method = getMethodBy("setJsonPrinter", symbolTablePrinter);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertFalse(method.isEmptyCDParameters());
    assertEquals(1, method.getCDParameterList().size());
    assertEquals("printer", method.getCDParameter(0).getName());
    assertDeepEquals("de.monticore.symboltable.serialization.JsonPrinter", method.getCDParameter(0).getMCType());
  }

  @Test
  public void testSerializeFooMethod() {
    ASTCDMethod method = getMethodBy("serializeFoo", symbolTablePrinter);
    assertDeepEquals(PROTECTED, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertFalse(method.isEmptyCDParameters());
    assertTrue(method.getCDParameterList().size() == 1);
    assertTrue(method.getCDParameter(0).getName().equals("node"));
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._symboltable.FooSymbol", method.getCDParameter(0).getMCType());
  }

  @Test
  public void testSerializeLocalSymbolsMethod(){
    ASTCDMethod method = getMethodBy("serializeLocalSymbols", symbolTablePrinter);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertFalse(method.isEmptyCDParameters());
    assertEquals(1, method.getCDParameterList().size());
    assertEquals("node", method.getCDParameter(0).getName());
    ASTMCType parameterType = this.mcTypeFacade.createQualifiedType(I_AUTOMATON_SCOPE);
    assertDeepEquals(parameterType, method.getCDParameter(0).getMCType());
  }

  @Test
  public void testSerializeFooExtraAttributeMethod() {
    ASTCDMethod method = getMethodBy("serializeFooExtraAttribute", symbolTablePrinter);
    assertDeepEquals(PROTECTED, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertFalse(method.isEmptyCDParameters());
    assertTrue(method.getCDParameterList().size() == 1);
    assertTrue(method.getCDParameter(0).getName().equals("extraAttribute"));
    assertDeepEquals("boolean", method.getCDParameter(0).getMCType());
  }

  @Test
  public void testSerializeFooFooMethod() {
    ASTCDMethod method = getMethodBy("serializeFooFoo", symbolTablePrinter);
    assertDeepEquals(PROTECTED, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertFalse(method.isEmptyCDParameters());
    assertTrue(method.getCDParameterList().size() == 1);
    assertTrue(method.getCDParameter(0).getName().equals("foo"));
    assertDeepEquals("List<String>", method.getCDParameter(0).getMCType());
  }

  @Test
  public void testSerializeFooBlaMethod() {
    ASTCDMethod method = getMethodBy("serializeFooBla", symbolTablePrinter);
    assertDeepEquals(PROTECTED, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertFalse(method.isEmptyCDParameters());
    assertTrue(method.getCDParameterList().size() == 1);
    assertTrue(method.getCDParameter(0).getName().equals("bla"));
    assertDeepEquals("Optional<Integer>", method.getCDParameter(0).getMCType());
  }

  @Test
  public void testSerializeStateMethod() {
    ASTCDMethod method = getMethodBy("serializeState", symbolTablePrinter);
    assertDeepEquals(PROTECTED, method.getModifier());
    assertFalse(method.getMCReturnType().isPresentMCType());

    assertTrue(!method.isEmptyCDParameters());
    assertTrue(method.getCDParameterList().size() == 1);
    assertTrue(method.getCDParameter(0).getName().equals("node"));
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._symboltable.StateSymbol", method.getCDParameter(0).getMCType());
  }

  @Test
  public void testGetRealThisMethod() {
    ASTCDMethod method = getMethodBy("getRealThis", symbolTablePrinter);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("AutomatonSymbolTablePrinter", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetSerializedStringMethod() {
    ASTCDMethod method = getMethodBy("getSerializedString", symbolTablePrinter);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testFilterRelevantSubScopesMethod() {
    ASTCDMethod method = getMethodBy("filterRelevantSubScopes", symbolTablePrinter);
    assertDeepEquals(PROTECTED, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertListOf(I_AUTOMATON_SCOPE, method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertListOf("? extends " + I_AUTOMATON_SCOPE, method.getCDParameter(0).getMCType());
    assertEquals("subScopes", method.getCDParameter(0).getName());
  }

  @Test
  public void testHasSymbolsInSubScopesMethod() {
    ASTCDMethod method = getMethodBy("hasSymbolsInSubScopes", symbolTablePrinter);
    assertDeepEquals(PROTECTED, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(mcTypeFacade.createQualifiedType(I_AUTOMATON_SCOPE),
        method.getCDParameter(0).getMCType());
    assertEquals("scope", method.getCDParameter(0).getName());
  }

  @Test
  public void testAddScopeSpanningSymbolMethod() {
    ASTCDMethod method = getMethodBy("addScopeSpanningSymbol", symbolTablePrinter);
    assertDeepEquals(PROTECTED, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(mcTypeFacade.createQualifiedType(I_SCOPE_SPANNING_SYMBOL), method.getCDParameter(0).getMCType());
    assertEquals("spanningSymbol", method.getCDParameter(0).getName());
  }

  @Test
  public void testVisitArtifactScopeMethod() {
    List<ASTCDMethod> methodList = getMethodsBy("visit", symbolTablePrinter);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(AUTOMATON_ARTIFACT_SCOPE);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(mcTypeFacade.createQualifiedType(AUTOMATON_ARTIFACT_SCOPE),
        method.getCDParameter(0).getMCType());
    assertEquals("node", method.getCDParameter(0).getName());
  }

  @Test
  public void testVisitScopeMethod() {
    List<ASTCDMethod> methodList = getMethodsBy("visit", symbolTablePrinter);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(AUTOMATON_SCOPE);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(mcTypeFacade.createQualifiedType(AUTOMATON_SCOPE),
        method.getCDParameter(0).getMCType());
    assertEquals("node", method.getCDParameter(0).getName());
  }

  @Test
  public void testTraverseArtifactScopeMethod() {
    List<ASTCDMethod> methodList = getMethodsBy("traverse", symbolTablePrinter);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(AUTOMATON_ARTIFACT_SCOPE);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(mcTypeFacade.createQualifiedType(AUTOMATON_ARTIFACT_SCOPE),
        method.getCDParameter(0).getMCType());
    assertEquals("node", method.getCDParameter(0).getName());
  }

  @Test
  public void testTraverseScopeMethod() {
    List<ASTCDMethod> methodList = getMethodsBy("traverse", symbolTablePrinter);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(AUTOMATON_SCOPE);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(mcTypeFacade.createQualifiedType(AUTOMATON_SCOPE),
        method.getCDParameter(0).getMCType());
    assertEquals("node", method.getCDParameter(0).getName());
  }

  @Test
  public void testEndVisitArtifactScopeMethod() {
    List<ASTCDMethod> methodList = getMethodsBy("endVisit", symbolTablePrinter);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(AUTOMATON_ARTIFACT_SCOPE);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(mcTypeFacade.createQualifiedType(AUTOMATON_ARTIFACT_SCOPE),
        method.getCDParameter(0).getMCType());
    assertEquals("node", method.getCDParameter(0).getName());
  }

  @Test
  public void testEndVisitScopeMethod() {
    List<ASTCDMethod> methodList = getMethodsBy("endVisit", symbolTablePrinter);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(AUTOMATON_SCOPE);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(mcTypeFacade.createQualifiedType(AUTOMATON_SCOPE),
        method.getCDParameter(0).getMCType());
    assertEquals("node", method.getCDParameter(0).getName());
  }

  @Test
  public void testEndVisitAutomatonSymbolMethod() {
    List<ASTCDMethod> methodList = getMethodsBy("endVisit", symbolTablePrinter);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(AUTOMATON_SYMBOL);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(mcTypeFacade.createQualifiedType(AUTOMATON_SYMBOL),
        method.getCDParameter(0).getMCType());
    assertEquals("node", method.getCDParameter(0).getName());
  }

  @Test
  public void testVisitAutomatonSymbolMethod() {
    List<ASTCDMethod> methodList = getMethodsBy("visit", symbolTablePrinter);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(AUTOMATON_SYMBOL);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(mcTypeFacade.createQualifiedType(AUTOMATON_SYMBOL),
        method.getCDParameter(0).getMCType());
    assertEquals("node", method.getCDParameter(0).getName());
  }

  @Test
  public void testEndVisitStateSymbolMethod() {
    List<ASTCDMethod> methodList = getMethodsBy("endVisit", symbolTablePrinter);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(STATE_SYMBOL);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(mcTypeFacade.createQualifiedType(STATE_SYMBOL),
        method.getCDParameter(0).getMCType());
    assertEquals("node", method.getCDParameter(0).getName());
  }

  @Test
  public void testVisitStateSymbolMethod() {
    List<ASTCDMethod> methodList = getMethodsBy("visit", symbolTablePrinter);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(STATE_SYMBOL);
    assertTrue(
        methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1,
        methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType()))
            .count());
    ASTCDMethod method = methodList.stream()
        .filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(mcTypeFacade.createQualifiedType(STATE_SYMBOL),
        method.getCDParameter(0).getMCType());
    assertEquals("node", method.getCDParameter(0).getName());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, symbolTablePrinter, symbolTablePrinter);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  }
}
