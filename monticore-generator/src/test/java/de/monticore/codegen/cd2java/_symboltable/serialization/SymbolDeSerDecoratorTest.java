/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.serialization;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.MCTypeFacade;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.monticore.codegen.cd2java.DecoratorAssert.*;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static org.junit.Assert.*;

public class SymbolDeSerDecoratorTest extends DecoratorTestCase {

  private ASTCDClass symbolClassAutomaton;

  private ASTCDClass symbolClassFoo;

  private GlobalExtensionManagement glex;

  private MCTypeFacade mcTypeFacade;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  private static final String AUTOMATON_SYMBOL = "de.monticore.codegen.symboltable.automatonsymbolcd._symboltable.AutomatonSymbol";

  private static final String FOO_SYMBOL = "de.monticore.codegen.symboltable.automatonsymbolcd._symboltable.FooSymbol";

  private static final String I_AUTOMATON_SCOPE = "de.monticore.codegen.symboltable.automatonsymbolcd._symboltable.IAutomatonSymbolCDScope";

  private static final String AUTOMATON_SYMBOL_TABLE_PRINTER = "de.monticore.codegen.symboltable.automatonsymbolcd._symboltable.AutomatonSymbolCDSymbols2Json";

  private static final String JSON_OBJECT = "de.monticore.symboltable.serialization.json.JsonObject";

  @Before
  public void setUp(){
    Log.init();
    this.mcTypeFacade = MCTypeFacade.getInstance();
    this.glex = new GlobalExtensionManagement();
    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "AutomatonSymbolCD");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));

    SymbolDeSerDecorator decorator = new SymbolDeSerDecorator(glex, new SymbolTableService(decoratedCompilationUnit));
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
  }

  @Test
  public void testClassNameAutomatonSymbol(){
    assertEquals("AutomatonSymbolDeSer", symbolClassAutomaton.getName());
  }

  @Test
  public void testSuperInterfaceCount(){
    assertEquals(1, symbolClassAutomaton.sizeInterface());
  }

  @Test
  public void testConstructorCount(){
    assertEquals(1, symbolClassAutomaton.sizeCDConstructors());
  }

  @Test
  public void testConstructors(){
    ASTCDConstructor constructor = symbolClassAutomaton.getCDConstructor(0);
    assertDeepEquals(CDModifier.PUBLIC, constructor.getModifier());
    assertTrue(constructor.isEmptyCDParameters());
  }

  @Test
  public void testAttributeCount(){
    assertEquals(1, symbolClassAutomaton.sizeCDAttributes());
  }

  @Test
  public void testAttributes(){
    List<ASTCDAttribute> attributeList = symbolClassAutomaton.getCDAttributeList();
    assertDeepEquals(CDModifier.PROTECTED, attributeList.get(0).getModifier());
    assertEquals("symbolTablePrinter", attributeList.get(0).getName());
    assertDeepEquals(AUTOMATON_SYMBOL_TABLE_PRINTER, attributeList.get(0).getMCType());
  }

  @Test
  public void testMethodCount(){
    assertEquals(5, symbolClassAutomaton.sizeCDMethods());
  }

  @Test
  public void testGetSerializedKindMethod(){
    ASTCDMethod method = getMethodBy("getSerializedKind", symbolClassAutomaton);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals(0, method.sizeException());
    assertEquals(0, method.sizeCDParameters());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());
  }

  @Test
  public void testSerializeMethod(){
    ASTCDMethod method = getMethodBy("serialize", symbolClassAutomaton);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals(0, method.sizeException());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("toSerialize", parameter.getName());
    assertDeepEquals(AUTOMATON_SYMBOL, parameter.getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());
  }

  @Test
  public void testDeserializeMethod(){
    ASTCDMethod method = getMethodBy("deserialize", symbolClassAutomaton);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals(0, method.sizeException());
    assertEquals(1, method.sizeCDParameters());
    List<ASTCDParameter> parameterList = method.getCDParameterList();
    assertEquals("serialized", parameterList.get(0).getName());
    assertDeepEquals(String.class, parameterList.get(0).getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(AUTOMATON_SYMBOL, method.getMCReturnType().getMCType());
  }

  @Test
  public void testDeserializeAutomatonSymbolMethod(){
    ASTCDMethod method = getMethodBy("deserializeAutomatonSymbol", symbolClassAutomaton);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals(0, method.sizeException());
    assertEquals(1, method.sizeCDParameters());
    List<ASTCDParameter> parameterList = method.getCDParameterList();
    assertEquals("symbolJson", parameterList.get(0).getName());
    assertDeepEquals(JSON_OBJECT, parameterList.get(0).getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(AUTOMATON_SYMBOL, method.getMCReturnType().getMCType());
  }

  @Test
  public void testDeserializeAdditionalAttributesMethod(){
    ASTCDMethod method = getMethodBy("deserializeAddons", symbolClassAutomaton);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertEquals(0, method.sizeException());
    assertEquals(2, method.sizeCDParameters());
    List<ASTCDParameter> parameterList = method.getCDParameterList();
    assertEquals("symbol", parameterList.get(0).getName());
    assertDeepEquals(AUTOMATON_SYMBOL, parameterList.get(0).getMCType());
    assertEquals("symbolJson", parameterList.get(1).getName());
    assertDeepEquals(JSON_OBJECT, parameterList.get(1).getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testClassNameFooSymbol(){
    assertEquals("FooSymbolDeSer", symbolClassFoo.getName());
  }

  @Test
  public void testNoSuperInterfaceFoo(){
    assertEquals(1, symbolClassFoo.sizeInterface());
  }

  @Test
  public void testConstructorCountFoo(){
    assertEquals(1, symbolClassFoo.sizeCDConstructors());
  }

  @Test
  public void testConstructorsFoo(){
    ASTCDConstructor constructor = symbolClassFoo.getCDConstructor(0);
    assertDeepEquals(CDModifier.PUBLIC, constructor.getModifier());
    assertTrue(constructor.isEmptyCDParameters());
  }

  @Test
  public void testAttributeCountFoo(){
    assertEquals(1, symbolClassFoo.sizeCDAttributes());
  }

  @Test
  public void testAttributesFoo(){
    List<ASTCDAttribute> attributeList = symbolClassFoo.getCDAttributeList();
    assertDeepEquals(CDModifier.PROTECTED, attributeList.get(0).getModifier());
    assertEquals("symbolTablePrinter", attributeList.get(0).getName());
    assertDeepEquals(AUTOMATON_SYMBOL_TABLE_PRINTER, attributeList.get(0).getMCType());
  }

  @Test
  public void testMethodCountFoo(){
    assertEquals(5, symbolClassAutomaton.sizeCDMethods());
  }

  @Test
  public void testGetSerializedKindMethodFoo(){
    ASTCDMethod method = getMethodBy("getSerializedKind", symbolClassFoo);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals(0, method.sizeException());
    assertEquals(0, method.sizeCDParameters());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());
  }

  @Test
  public void testSerializeMethodFoo(){
    ASTCDMethod method = getMethodBy("serialize", symbolClassFoo);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals(0, method.sizeException());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("toSerialize", parameter.getName());
    assertDeepEquals(FOO_SYMBOL, parameter.getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());
  }

  @Test
  public void testDeserializeMethodFoo(){
    ASTCDMethod method = getMethodBy("deserialize", symbolClassFoo);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals(0, method.sizeException());
    assertEquals(1, method.sizeCDParameters());
    List<ASTCDParameter> parameterList = method.getCDParameterList();
    assertEquals("serialized", parameterList.get(0).getName());
    assertDeepEquals(String.class, parameterList.get(0).getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(FOO_SYMBOL, method.getMCReturnType().getMCType());
  }

  @Test
  public void testDeserializeFooSymbolMethod(){
    ASTCDMethod method = getMethodBy("deserializeFooSymbol", symbolClassFoo);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals(0, method.sizeException());
    assertEquals(1, method.sizeCDParameters());
    List<ASTCDParameter> parameterList = method.getCDParameterList();
    assertEquals("symbolJson", parameterList.get(0).getName());
    assertDeepEquals(JSON_OBJECT, parameterList.get(0).getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(FOO_SYMBOL, method.getMCReturnType().getMCType());
  }

  @Test
  public void testDeserializeAdditionalAttributesMethodFoo(){
    ASTCDMethod method = getMethodBy("deserializeAddons", symbolClassFoo);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertEquals(0, method.sizeException());
    assertEquals(2, method.sizeCDParameters());
    List<ASTCDParameter> parameterList = method.getCDParameterList();
    assertEquals("symbol", parameterList.get(0).getName());
    assertDeepEquals(FOO_SYMBOL, parameterList.get(0).getMCType());
    assertEquals("symbolJson", parameterList.get(1).getName());
    assertDeepEquals(JSON_OBJECT, parameterList.get(1).getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testDeserializeExtraAttribute(){
    ASTCDMethod method = getMethodBy("deserializeExtraAttribute", symbolClassFoo);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals(0, method.sizeException());
    assertEquals(1, method.sizeCDParameters());
    List<ASTCDParameter> parameterList = method.getCDParameterList();
    assertEquals("symbolJson", parameterList.get(0).getName());
    assertDeepEquals(JSON_OBJECT, parameterList.get(0).getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertBoolean(method.getMCReturnType().getMCType());
  }

  @Test
  public void testDeserializeFoo(){
    ASTCDMethod method = getMethodBy("deserializeFoo", symbolClassFoo);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals(0, method.sizeException());
    assertEquals(1, method.sizeCDParameters());
    List<ASTCDParameter> parameterList = method.getCDParameterList();
    assertEquals("symbolJson", parameterList.get(0).getName());
    assertDeepEquals(JSON_OBJECT, parameterList.get(0).getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertListOf(String.class, method.getMCReturnType().getMCType());
  }

  @Test
  public void testDeserializeBla(){
    ASTCDMethod method = getMethodBy("deserializeBla", symbolClassFoo);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals(0, method.sizeException());
    assertEquals(1, method.sizeCDParameters());
    List<ASTCDParameter> parameterList = method.getCDParameterList();
    assertEquals("symbolJson", parameterList.get(0).getName());
    assertDeepEquals(JSON_OBJECT, parameterList.get(0).getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertOptionalOf(Integer.class, method.getMCReturnType().getMCType());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, symbolClassAutomaton, symbolClassAutomaton);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  }

  @Test
  public void testGeneratedCodeFoo() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, symbolClassFoo, symbolClassFoo);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  }
}
