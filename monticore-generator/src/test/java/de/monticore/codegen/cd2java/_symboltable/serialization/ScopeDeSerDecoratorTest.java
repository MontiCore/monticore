package de.monticore.codegen.cd2java._symboltable.serialization;

import com.github.javaparser.StaticJavaParser;
import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
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
import static de.monticore.codegen.cd2java.factories.CDModifier.*;
import static org.junit.Assert.*;

public class ScopeDeSerDecoratorTest extends DecoratorTestCase {

  private ASTCDClass scopeDeSer;

  private GlobalExtensionManagement glex;

  private CDTypeFacade cdTypeFacade;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  private static final String AUTOMATON_SYMBOL_DE_SER = "AutomatonSymbolDeSer";

  private static final String STATE_SYMBOL_DE_SER = "StateSymbolDeSer";

  private static final String FOO_SYMBOL_DE_SER = "FooSymbolDeSer";

  private static final String I_AUTOMATON_SCOPE = "de.monticore.codegen.symboltable.automatonsymbolcd._symboltable.IAutomatonSymbolCDScope";

  private static final String AUTOMATON_SCOPE = "de.monticore.codegen.symboltable.automatonsymbolcd._symboltable.AutomatonSymbolCDScope";

  private static final String AUTOMATON_ARTIFACT_SCOPE = "de.monticore.codegen.symboltable.automatonsymbolcd._symboltable.AutomatonSymbolCDArtifactScope";

  private static final String AUTOMATON_LANGUAGE = "de.monticore.codegen.symboltable.automatonsymbolcd._symboltable.AutomatonSymbolCDLanguage";

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

    ScopeDeSerDecorator decorator = new ScopeDeSerDecorator(this.glex, new SymbolTableService(decoratedCompilationUnit));
    this.scopeDeSer = decorator.decorate(decoratedCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  // ScopeSpanningSymbol

  @Test
  public void testClassNameAutomatonSymbol() {
    assertEquals("AutomatonSymbolCDScopeDeSer", scopeDeSer.getName());
  }

  @Test
  public void testSuperInterfacesCountAutomatonSymbol() {
    assertEquals(1, scopeDeSer.sizeInterfaces());
  }

  @Test
  public void testSuperInterfacesAutomatonSymbol() {
    assertDeepEquals("de.monticore.symboltable.serialization.IDeSer<de.monticore.codegen.symboltable.automatonsymbolcd._symboltable.IAutomatonSymbolCDScope>", scopeDeSer.getInterface(0));
  }

  @Test
  public void testNoSuperClass() {
    assertFalse(scopeDeSer.isPresentSuperclass());
  }

  @Test
  public void testNoConstructor() {
    assertTrue(scopeDeSer.isEmptyCDConstructors());
  }

  @Test
  public void testAttributeSize() {
    assertEquals(3, scopeDeSer.sizeCDAttributes());
  }

  @Test
  public void testAutomatonSymbolDeSerAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("automatonSymbolDeSer", scopeDeSer);
    assertDeepEquals(PACKAGE_PRIVATE, astcdAttribute.getModifier());
    assertDeepEquals(AUTOMATON_SYMBOL_DE_SER, astcdAttribute.getMCType());
  }

  @Test
  public void testFooSymbolDeSerAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("fooSymbolDeSer", scopeDeSer);
    assertDeepEquals(PACKAGE_PRIVATE, astcdAttribute.getModifier());
    assertDeepEquals(FOO_SYMBOL_DE_SER, astcdAttribute.getMCType());
  }

  @Test
  public void testStateSymbolDeSerAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("stateSymbolDeSer", scopeDeSer);
    assertDeepEquals(PACKAGE_PRIVATE, astcdAttribute.getModifier());
    assertDeepEquals(STATE_SYMBOL_DE_SER, astcdAttribute.getMCType());
  }

  @Test
  public void testMethods() {
    assertEquals(15, scopeDeSer.getCDMethodList().size());
  }

  @Test
  public void testStoreMethod() {
    ASTCDMethod method = getMethodBy("store", scopeDeSer);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());


    assertEquals(3, method.sizeCDParameters());
    assertDeepEquals(AUTOMATON_ARTIFACT_SCOPE, method.getCDParameter(0).getMCType());
    assertEquals("as", method.getCDParameter(0).getName());
    assertDeepEquals(AUTOMATON_LANGUAGE, method.getCDParameter(1).getMCType());
    assertEquals("lang", method.getCDParameter(1).getName());
    assertDeepEquals(String.class, method.getCDParameter(2).getMCType());
    assertEquals("symbolPath", method.getCDParameter(2).getName());
  }

  @Test
  public void testGetSerializedKindMethod() {
    ASTCDMethod method = getMethodBy("getSerializedKind", scopeDeSer);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetSerializedASKindMethod() {
    ASTCDMethod method = getMethodBy("getSerializedASKind", scopeDeSer);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSerializeMethod() {
    ASTCDMethod method = getMethodBy("serialize", scopeDeSer);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(cdTypeFacade.createQualifiedType(I_AUTOMATON_SCOPE),
        method.getCDParameter(0).getMCType());
    assertEquals("toSerialize", method.getCDParameter(0).getName());
  }

  @Test
  public void testDeserializeStringMethod() {
    List<ASTCDMethod> methods = getMethodsBy("deserialize", 1, scopeDeSer);
    ASTMCType astType = this.cdTypeFacade.createStringType();

    assertTrue(methods.stream().anyMatch(m -> m.getCDParameter(0).getMCType()
        .deepEquals(astType)));
    Optional<ASTCDMethod> methodOpt = methods.stream().filter(m -> m.getCDParameter(0).getMCType()
        .deepEquals(astType)).findFirst();
    assertTrue(methodOpt.isPresent());
    ASTCDMethod method = methodOpt.get();
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertOptionalOf(I_AUTOMATON_SCOPE, method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("serialized", method.getCDParameter(0).getName());
  }

  @Test
  public void testDeserializeJsonObjectMethod() {
    List<ASTCDMethod> methods = getMethodsBy("deserialize", 1, scopeDeSer);
    ASTMCType astType = this.cdTypeFacade.createQualifiedType("de.monticore.symboltable.serialization.json.JsonObject");

    assertTrue(methods.stream().anyMatch(m -> m.getCDParameter(0).getMCType()
        .deepEquals(astType)));
    Optional<ASTCDMethod> methodOpt = methods.stream().filter(m -> m.getCDParameter(0).getMCType()
        .deepEquals(astType)).findFirst();
    assertTrue(methodOpt.isPresent());
    ASTCDMethod method = methodOpt.get();
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertOptionalOf(I_AUTOMATON_SCOPE, method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(astType, method.getCDParameter(0).getMCType());
    assertEquals("scopeJson", method.getCDParameter(0).getName());
  }

  @Test
  public void testDeserializeScopeMethod() {
    ASTCDMethod method = getMethodBy("deserializeAutomatonSymbolCDScope", scopeDeSer);
    assertDeepEquals(PROTECTED, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(AUTOMATON_SCOPE, method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals("de.monticore.symboltable.serialization.json.JsonObject", method.getCDParameter(0).getMCType());
    assertEquals("scopeJson", method.getCDParameter(0).getName());
  }

  @Test
  public void testDeserializeArtifactScopeMethod() {
    ASTCDMethod method = getMethodBy("deserializeAutomatonSymbolCDArtifactScope", scopeDeSer);
    assertDeepEquals(PROTECTED, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(AUTOMATON_ARTIFACT_SCOPE, method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals("de.monticore.symboltable.serialization.json.JsonObject", method.getCDParameter(0).getMCType());
    assertEquals("scopeJson", method.getCDParameter(0).getName());
  }

  @Test
  public void testAddSymbolsMethod() {
    ASTCDMethod method = getMethodBy("addSymbols", scopeDeSer);
    assertDeepEquals(PROTECTED, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(2, method.sizeCDParameters());
    assertDeepEquals("de.monticore.symboltable.serialization.json.JsonObject", method.getCDParameter(0).getMCType());
    assertEquals("scopeJson", method.getCDParameter(0).getName());
    assertDeepEquals(AUTOMATON_SCOPE, method.getCDParameter(1).getMCType());
    assertEquals("scope", method.getCDParameter(1).getName());
  }


  @Test
  public void testAddAndLinkSubScopesMethod() {
    ASTCDMethod method = getMethodBy("addAndLinkSubScopes", scopeDeSer);
    assertDeepEquals(PROTECTED, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(2, method.sizeCDParameters());
    assertDeepEquals("de.monticore.symboltable.serialization.json.JsonObject", method.getCDParameter(0).getMCType());
    assertEquals("scopeJson", method.getCDParameter(0).getName());
    assertDeepEquals(AUTOMATON_SCOPE, method.getCDParameter(1).getMCType());
    assertEquals("scope", method.getCDParameter(1).getName());
  }

  @Test
  public void testAddAndLinkSpanningSymbolMethod() {
    ASTCDMethod method = getMethodBy("addAndLinkSpanningSymbol", scopeDeSer);
    assertDeepEquals(PROTECTED, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(3, method.sizeCDParameters());
    assertDeepEquals("de.monticore.symboltable.serialization.json.JsonObject", method.getCDParameter(0).getMCType());
    assertEquals("subScopeJson", method.getCDParameter(0).getName());
    assertDeepEquals(I_AUTOMATON_SCOPE, method.getCDParameter(1).getMCType());
    assertEquals("subScope", method.getCDParameter(1).getName());
    assertDeepEquals(AUTOMATON_SCOPE, method.getCDParameter(2).getMCType());
    assertEquals("scope", method.getCDParameter(2).getName());
  }

  @Test
  public void testDeserializeAutomatonSymbolMethod() {
    ASTCDMethod method = getMethodBy("deserializeAutomatonSymbol", scopeDeSer);
    assertDeepEquals(PROTECTED, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(2, method.sizeCDParameters());
    assertDeepEquals("de.monticore.symboltable.serialization.json.JsonObject", method.getCDParameter(0).getMCType());
    assertEquals("symbolJson", method.getCDParameter(0).getName());
    assertDeepEquals(AUTOMATON_SCOPE, method.getCDParameter(1).getMCType());
    assertEquals("scope", method.getCDParameter(1).getName());
  }


  @Test
  public void testDeserializeStateSymbolMethod() {
    ASTCDMethod method = getMethodBy("deserializeStateSymbol", scopeDeSer);
    assertDeepEquals(PROTECTED, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(2, method.sizeCDParameters());
    assertDeepEquals("de.monticore.symboltable.serialization.json.JsonObject", method.getCDParameter(0).getMCType());
    assertEquals("symbolJson", method.getCDParameter(0).getName());
    assertDeepEquals(AUTOMATON_SCOPE, method.getCDParameter(1).getMCType());
    assertEquals("scope", method.getCDParameter(1).getName());
  }


  @Test
  public void testDeserializeFooSymbolMethod() {
    ASTCDMethod method = getMethodBy("deserializeFooSymbol", scopeDeSer);
    assertDeepEquals(PROTECTED, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(2, method.sizeCDParameters());
    assertDeepEquals("de.monticore.symboltable.serialization.json.JsonObject", method.getCDParameter(0).getMCType());
    assertEquals("symbolJson", method.getCDParameter(0).getName());
    assertDeepEquals(AUTOMATON_SCOPE, method.getCDParameter(1).getMCType());
    assertEquals("scope", method.getCDParameter(1).getName());
  }

  @Test
  public void testDeserializeAdditionalAttributesMethod() {
    ASTCDMethod method = getMethodBy("deserializeAdditionalAttributes", scopeDeSer);
    assertDeepEquals(PROTECTED, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(2, method.sizeCDParameters());
    assertDeepEquals(I_AUTOMATON_SCOPE, method.getCDParameter(0).getMCType());
    assertEquals("scope", method.getCDParameter(0).getName());
    assertDeepEquals("de.monticore.symboltable.serialization.json.JsonObject", method.getCDParameter(1).getMCType());
    assertEquals("scopeJson", method.getCDParameter(1).getName());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, scopeDeSer, scopeDeSer);
    System.out.println(sb.toString());
    StaticJavaParser.parse(sb.toString());
  }
}
