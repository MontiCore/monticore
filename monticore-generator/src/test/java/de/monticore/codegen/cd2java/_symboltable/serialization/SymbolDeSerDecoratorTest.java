package de.monticore.codegen.cd2java._symboltable.serialization;

import com.github.javaparser.StaticJavaParser;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.factories.CDTypeFacade;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertOptionalOf;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
import static org.junit.Assert.*;

public class SymbolDeSerDecoratorTest extends DecoratorTestCase {

  private ASTCDClass symbolDeSer;

  private GlobalExtensionManagement glex;

  private CDTypeFacade cdTypeFacade;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  private static final String AUTOMATON_SYMBOL = "de.monticore.codegen.symboltable.automatonsymbolcd._symboltable.AutomatonSymbol";

  @Before
  public void setUp() {
    Log.init();
    this.cdTypeFacade = CDTypeFacade.getInstance();
    this.glex = new GlobalExtensionManagement();

    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "AutomatonSymbolCD");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));

    SymbolDeSerDecorator decorator = new SymbolDeSerDecorator(this.glex, new SymbolTableService(decoratedCompilationUnit));
    ASTCDClass automatonClass = getClassBy("Automaton", decoratedCompilationUnit);
    this.symbolDeSer = decorator.decorate(automatonClass);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testClassNameAutomatonSymbol() {
    assertEquals("AutomatonSymbolDeSer", symbolDeSer.getName());
  }

  @Test
  public void testSuperInterfacesCountAutomatonSymbol() {
    assertEquals(1, symbolDeSer.sizeInterfaces());
  }

  @Test
  public void testSuperInterfacesAutomatonSymbol() {
    assertDeepEquals("de.monticore.symboltable.serialization.IDeSer<de.monticore.codegen.symboltable.automatonsymbolcd._symboltable.AutomatonSymbol>", symbolDeSer.getInterface(0));
  }

  @Test
  public void testNoSuperClass() {
    assertFalse(symbolDeSer.isPresentSuperclass());
  }

  @Test
  public void testNoConstructor() {
    assertTrue(symbolDeSer.isEmptyCDConstructors());
  }

  @Test
  public void testNoAttributes() {
    assertTrue(symbolDeSer.isEmptyCDAttributes());
  }


  @Test
  public void testMethods() {
    assertEquals(6, symbolDeSer.getCDMethodList().size());
  }

  @Test
  public void testGetSerializedKindMethod() {
    ASTCDMethod method = getMethodBy("getSerializedKind", symbolDeSer);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSerializeMethod() {
    ASTCDMethod method = getMethodBy("serialize", symbolDeSer);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(cdTypeFacade.createQualifiedType(AUTOMATON_SYMBOL),
        method.getCDParameter(0).getMCType());
    assertEquals("toSerialize", method.getCDParameter(0).getName());
  }

  @Test
  public void testDeserializeStringMethod() {
    List<ASTCDMethod> methods = getMethodsBy("deserialize", 1, symbolDeSer);
    ASTMCType astType = this.cdTypeFacade.createStringType();

    assertTrue(methods.stream().anyMatch(m -> m.getCDParameter(0).getMCType()
        .deepEquals(astType)));
    Optional<ASTCDMethod> methodOpt = methods.stream().filter(m -> m.getCDParameter(0).getMCType()
        .deepEquals(astType)).findFirst();
    assertTrue(methodOpt.isPresent());
    ASTCDMethod method = methodOpt.get();
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertOptionalOf(AUTOMATON_SYMBOL, method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("serialized", method.getCDParameter(0).getName());
  }

  @Test
  public void testDeserializeJsonObjectMethod() {
    List<ASTCDMethod> methods = getMethodsBy("deserialize", 1, symbolDeSer);
    ASTMCType astType = this.cdTypeFacade.createQualifiedType("de.monticore.symboltable.serialization.json.JsonObject");

    assertTrue(methods.stream().anyMatch(m -> m.getCDParameter(0).getMCType()
        .deepEquals(astType)));
    Optional<ASTCDMethod> methodOpt = methods.stream().filter(m -> m.getCDParameter(0).getMCType()
        .deepEquals(astType)).findFirst();
    assertTrue(methodOpt.isPresent());
    ASTCDMethod method = methodOpt.get();
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertOptionalOf(AUTOMATON_SYMBOL, method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(astType, method.getCDParameter(0).getMCType());
    assertEquals("symbolJson", method.getCDParameter(0).getName());
  }

  @Test
  public void testDeserializeSymbolMethod() {
    ASTCDMethod method = getMethodBy("deserializeAutomatonSymbol", symbolDeSer);
    assertDeepEquals(PROTECTED, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(AUTOMATON_SYMBOL, method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals("de.monticore.symboltable.serialization.json.JsonObject", method.getCDParameter(0).getMCType());
    assertEquals("symbolJson", method.getCDParameter(0).getName());
  }

  @Test
  public void testDeserializeAdditionalAttributesMethod() {
    ASTCDMethod method = getMethodBy("deserializeAdditionalAttributes", symbolDeSer);
    assertDeepEquals(PROTECTED, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(2, method.sizeCDParameters());
    assertDeepEquals(AUTOMATON_SYMBOL, method.getCDParameter(0).getMCType());
    assertEquals("symbol", method.getCDParameter(0).getName());
    assertDeepEquals("de.monticore.symboltable.serialization.json.JsonObject", method.getCDParameter(1).getMCType());
    assertEquals("symbolJson", method.getCDParameter(1).getName());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, symbolDeSer, symbolDeSer);
    StaticJavaParser.parse(sb.toString());
  }

}
