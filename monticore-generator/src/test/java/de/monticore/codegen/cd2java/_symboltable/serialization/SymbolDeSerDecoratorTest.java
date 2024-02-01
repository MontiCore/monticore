/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.serialization;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.io.paths.MCPath;
import de.monticore.types.MCTypeFacade;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertBoolean;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertListOf;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertOptionalOf;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodsBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SymbolDeSerDecoratorTest extends DecoratorTestCase {

  private ASTCDClass symbolClassAutomaton;

  private ASTCDClass symbolClassFoo;

  private MCTypeFacade mcTypeFacade;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  private static final String AUTOMATON_SYMBOL = "de.monticore.codegen.symboltable.automatonsymbolcd._symboltable.AutomatonSymbol";

  private static final String FOO_SYMBOL = "de.monticore.codegen.symboltable.automatonsymbolcd._symboltable.FooSymbol";

  private static final String AUTOMATON_SYMBOLS_2_JSON = "de.monticore.codegen.symboltable.automatonsymbolcd._symboltable.AutomatonSymbolCDSymbols2Json";

  private static final String JSON_OBJECT = "de.monticore.symboltable.serialization.json.JsonObject";

  private static final String IAUTOMATON_SCOPE = "de.monticore.codegen.symboltable.automatonsymbolcd._symboltable.IAutomatonSymbolCDScope";

  @Before
  public void setUp() {
    LogStub.init();
    Log.enableFailQuick(false);
    this.mcTypeFacade = MCTypeFacade.getInstance();
    decoratedCompilationUnit = this
        .parse("de", "monticore", "codegen", "symboltable", "AutomatonSymbolCD");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));

    SymbolDeSerDecorator decorator = new SymbolDeSerDecorator(glex,
        new SymbolTableService(decoratedCompilationUnit),
        new MCPath());
    //creates ScopeSpanningSymbol
    ASTCDClass automatonClass = getClassBy("Automaton", decoratedCompilationUnit);
    this.symbolClassAutomaton = decorator.decorate(automatonClass);
    //creates fooSymbolRef
    ASTCDClass fooClass = getClassBy("Foo", decoratedCompilationUnit);
    this.symbolClassFoo = decorator.decorate(fooClass);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testClassNameAutomatonSymbol() {
    assertEquals("AutomatonSymbolDeSer", symbolClassAutomaton.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSuperInterfaceCount() {
    assertEquals(1, symbolClassAutomaton.getInterfaceList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testConstructorCount() {
    assertEquals(0, symbolClassAutomaton.getCDConstructorList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAttributeCount() {
    assertEquals(0, symbolClassAutomaton.getCDAttributeList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMethodCount(){
    assertEquals(7, symbolClassAutomaton.getCDMethodList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetSerializedKindMethod() {
    ASTCDMethod method = getMethodBy("getSerializedKind", symbolClassAutomaton);
    assertDeepEquals(CDModifier.PUBLIC.build(), method.getModifier());
    assertFalse(method.isPresentCDThrowsDeclaration());
    assertEquals(0, method.sizeCDParameters());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSerializeMethod() {
    ASTCDMethod method = getMethodBy("serialize", symbolClassAutomaton);
    assertDeepEquals(CDModifier.PUBLIC.build(), method.getModifier());
    assertFalse(method.isPresentCDThrowsDeclaration());
    assertEquals(2, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("toSerialize", parameter.getName());
    assertDeepEquals(AUTOMATON_SYMBOL, parameter.getMCType());
    parameter = method.getCDParameter(1);
    assertEquals("s2j", parameter.getName());
    assertDeepEquals(AUTOMATON_SYMBOLS_2_JSON, parameter.getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDeserializeMethod() {
    List<ASTCDMethod> methods = getMethodsBy("deserialize", symbolClassAutomaton);

    ASTCDMethod method = methods.get(0);
    assertDeepEquals(CDModifier.PUBLIC.build(), method.getModifier());
    assertFalse(method.isPresentCDThrowsDeclaration());
    assertEquals(2, method.sizeCDParameters());
    List<ASTCDParameter> parameterList = method.getCDParameterList();
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(AUTOMATON_SYMBOL, method.getMCReturnType().getMCType());

    method = methods.get(1);
    assertDeepEquals(CDModifier.PUBLIC.build(), method.getModifier());
    assertFalse(method.isPresentCDThrowsDeclaration());
    assertEquals(1, method.sizeCDParameters());
    parameterList = method.getCDParameterList();
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(AUTOMATON_SYMBOL, method.getMCReturnType().getMCType());

    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDeserializeAdditionalAttributesMethod() {
    ASTCDMethod method = getMethodBy("deserializeAddons", symbolClassAutomaton);
    assertDeepEquals(CDModifier.PROTECTED.build(), method.getModifier());
    assertFalse(method.isPresentCDThrowsDeclaration());
    assertEquals(2, method.sizeCDParameters());
    List<ASTCDParameter> parameterList = method.getCDParameterList();
    assertEquals("symbol", parameterList.get(0).getName());
    assertDeepEquals(AUTOMATON_SYMBOL, parameterList.get(0).getMCType());
    assertEquals("symbolJson", parameterList.get(1).getName());
    assertDeepEquals(JSON_OBJECT, parameterList.get(1).getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testClassNameFooSymbol() {
    assertEquals("FooSymbolDeSer", symbolClassFoo.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testNoSuperInterfaceFoo() {
    assertEquals(1, symbolClassFoo.getInterfaceList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testConstructorCountFoo() {
    assertEquals(0, symbolClassFoo.getCDConstructorList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAttributeCountFoo() {
    assertEquals(0, symbolClassFoo.getCDAttributeList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMethodCountFoo() {
    assertEquals(16, symbolClassFoo.getCDMethodList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetSerializedKindMethodFoo() {
    ASTCDMethod method = getMethodBy("getSerializedKind", symbolClassFoo);
    assertDeepEquals(CDModifier.PUBLIC.build(), method.getModifier());
    assertFalse(method.isPresentCDThrowsDeclaration());
    assertEquals(0, method.sizeCDParameters());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSerializeMethodFoo() {
    ASTCDMethod method = getMethodBy("serialize", symbolClassFoo);
    assertDeepEquals(CDModifier.PUBLIC.build(), method.getModifier());
    assertFalse(method.isPresentCDThrowsDeclaration());
    assertEquals(2, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("toSerialize", parameter.getName());
    assertDeepEquals(FOO_SYMBOL, parameter.getMCType());
    parameter = method.getCDParameter(1);
    assertEquals("s2j", parameter.getName());
    assertDeepEquals(AUTOMATON_SYMBOLS_2_JSON, parameter.getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDeserializeMethodFoo() {
    List<ASTCDMethod> methods = getMethodsBy("deserialize", symbolClassFoo);

    ASTCDMethod method = methods.get(0);
    assertDeepEquals(CDModifier.PUBLIC.build(), method.getModifier());
    assertFalse(method.isPresentCDThrowsDeclaration());
    assertEquals(2, method.sizeCDParameters());
    List<ASTCDParameter> parameterList = method.getCDParameterList();
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(FOO_SYMBOL, method.getMCReturnType().getMCType());

    method = methods.get(1);
    assertDeepEquals(CDModifier.PUBLIC.build(), method.getModifier());
    assertFalse(method.isPresentCDThrowsDeclaration());
    assertEquals(1, method.sizeCDParameters());
    parameterList = method.getCDParameterList();
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(FOO_SYMBOL, method.getMCReturnType().getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDeserializeAdditionalAttributesMethodFoo() {
    ASTCDMethod method = getMethodBy("deserializeAddons", symbolClassFoo);
    assertDeepEquals(CDModifier.PROTECTED.build(), method.getModifier());
    assertFalse(method.isPresentCDThrowsDeclaration());
    assertEquals(2, method.sizeCDParameters());
    List<ASTCDParameter> parameterList = method.getCDParameterList();
    assertEquals("symbol", parameterList.get(0).getName());
    assertDeepEquals(FOO_SYMBOL, parameterList.get(0).getMCType());
    assertEquals("symbolJson", parameterList.get(1).getName());
    assertDeepEquals(JSON_OBJECT, parameterList.get(1).getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSerializeAddonsMethod() {
    ASTCDMethod method = getMethodBy("serializeAddons", symbolClassFoo);
    assertDeepEquals(CDModifier.PROTECTED.build(), method.getModifier());
    assertFalse(method.isPresentCDThrowsDeclaration());
    assertEquals(2, method.sizeCDParameters());
    List<ASTCDParameter> parameterList = method.getCDParameterList();
    assertEquals("toSerialize", parameterList.get(0).getName());
    assertDeepEquals(FOO_SYMBOL, parameterList.get(0).getMCType());
    assertEquals("s2j", parameterList.get(1).getName());
    assertDeepEquals(AUTOMATON_SYMBOLS_2_JSON, parameterList.get(1).getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDeserializeExtraAttribute() {
    List<ASTCDMethod> methods = getMethodsBy("deserializeExtraAttribute", symbolClassFoo);
    ASTCDMethod method = methods.get(0);
    assertDeepEquals(CDModifier.PROTECTED.build(), method.getModifier());
    assertFalse(method.isPresentCDThrowsDeclaration());
    assertEquals(1, method.sizeCDParameters());
    List<ASTCDParameter> parameterList = method.getCDParameterList();
    assertEquals("symbolJson", parameterList.get(0).getName());
    assertDeepEquals(JSON_OBJECT, parameterList.get(0).getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertBoolean(method.getMCReturnType().getMCType());

    method = methods.get(1);
    assertDeepEquals(CDModifier.PROTECTED.build(), method.getModifier());
    assertFalse(method.isPresentCDThrowsDeclaration());
    assertEquals(2, method.sizeCDParameters());
    parameterList = method.getCDParameterList();
    assertEquals("scope", parameterList.get(0).getName());
    assertDeepEquals(IAUTOMATON_SCOPE, parameterList.get(0).getMCType());
    assertEquals("symbolJson", parameterList.get(1).getName());
    assertDeepEquals(JSON_OBJECT, parameterList.get(1).getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertBoolean(method.getMCReturnType().getMCType());

    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDeserializeFoo() {
    List<ASTCDMethod> methods = getMethodsBy("deserializeFoo", symbolClassFoo);
    ASTCDMethod method = methods.get(0);
    assertDeepEquals(CDModifier.PROTECTED.build(), method.getModifier());
    assertFalse(method.isPresentCDThrowsDeclaration());
    assertEquals(1, method.sizeCDParameters());
    List<ASTCDParameter> parameterList = method.getCDParameterList();
    assertEquals("symbolJson", parameterList.get(0).getName());
    assertDeepEquals(JSON_OBJECT, parameterList.get(0).getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertListOf(String.class, method.getMCReturnType().getMCType());

    method = methods.get(1);
    assertDeepEquals(CDModifier.PROTECTED.build(), method.getModifier());
    assertFalse(method.isPresentCDThrowsDeclaration());
    assertEquals(2, method.sizeCDParameters());
    parameterList = method.getCDParameterList();
    assertEquals("scope", parameterList.get(0).getName());
    assertDeepEquals(IAUTOMATON_SCOPE, parameterList.get(0).getMCType());
    assertEquals("symbolJson", parameterList.get(1).getName());
    assertDeepEquals(JSON_OBJECT, parameterList.get(1).getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertListOf(String.class, method.getMCReturnType().getMCType());
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDeserializeBla() {
    List<ASTCDMethod> methods = getMethodsBy("deserializeBla", symbolClassFoo);
    ASTCDMethod method = methods.get(0);
    assertDeepEquals(CDModifier.PROTECTED.build(), method.getModifier());
    assertFalse(method.isPresentCDThrowsDeclaration());
    assertEquals(1, method.sizeCDParameters());
    List<ASTCDParameter> parameterList = method.getCDParameterList();
    assertEquals("symbolJson", parameterList.get(0).getName());
    assertDeepEquals(JSON_OBJECT, parameterList.get(0).getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertOptionalOf(Integer.class, method.getMCReturnType().getMCType());

    method = methods.get(1);
    assertDeepEquals(CDModifier.PROTECTED.build(), method.getModifier());
    assertFalse(method.isPresentCDThrowsDeclaration());
    assertEquals(2, method.sizeCDParameters());
    parameterList = method.getCDParameterList();
    assertEquals("scope", parameterList.get(0).getName());
    assertDeepEquals(IAUTOMATON_SCOPE, parameterList.get(0).getMCType());
    assertEquals("symbolJson", parameterList.get(1).getName());
    assertDeepEquals(JSON_OBJECT, parameterList.get(1).getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertOptionalOf(Integer.class, method.getMCReturnType().getMCType());

    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    CD4C.init(generatorSetup);
    StringBuilder sb = generatorEngine
        .generate(CD2JavaTemplates.CLASS, symbolClassAutomaton, packageDir);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGeneratedCodeFoo() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    CD4C.init(generatorSetup);
    StringBuilder sb = generatorEngine
        .generate(CD2JavaTemplates.CLASS, symbolClassFoo, packageDir);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
