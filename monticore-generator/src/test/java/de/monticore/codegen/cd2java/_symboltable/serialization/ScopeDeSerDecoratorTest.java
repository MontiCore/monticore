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
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.MCTypeFacade;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodsBy;
import static org.junit.Assert.*;

public class ScopeDeSerDecoratorTest extends DecoratorTestCase {

  private ASTCDClass scopeClass;

  private GlobalExtensionManagement glex;

  private MCTypeFacade mcTypeFacade;

  private ASTCDCompilationUnit decoratedSymbolCompilationUnit;

  private ASTCDCompilationUnit decoratedScopeCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  private static final String JSON_OBJECT = "de.monticore.symboltable.serialization.json.JsonObject";

  private static final String AUTOMATON_SCOPE = "de.monticore.codegen.symboltable.automaton._symboltable.AutomatonScope";

  private static final String AUTOMATON_ARTIFACT_SCOPE = "de.monticore.codegen.symboltable.automaton._symboltable.AutomatonArtifactScope";

  private static final String AUTOMATON_GLOBAL_SCOPE = "de.monticore.codegen.symboltable.automaton._symboltable.AutomatonGlobalScope";

  private static final String I_AUTOMATON_SCOPE = "de.monticore.codegen.symboltable.automaton._symboltable.IAutomatonScope";

  private static final String AUTOMATON_SYMBOL = "AutomatonSymbol";

  private static final String STATE_SYMBOL = "StateSymbol";

  private static final String FOO_SYMBOL = "FooSymbol";

  private static final String DESER = "DeSer";

  @Before
  public void setUp(){
    this.glex = new GlobalExtensionManagement();
    this.mcTypeFacade = MCTypeFacade.getInstance();

    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    ASTCDCompilationUnit astcdCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "Automaton");
    decoratedSymbolCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "AutomatonSymbolCD");
    decoratedScopeCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "AutomatonScopeCD");
    originalCompilationUnit = decoratedSymbolCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(astcdCompilationUnit));

    ScopeDeSerDecorator decorator = new ScopeDeSerDecorator(glex, new SymbolTableService(astcdCompilationUnit), new MethodDecorator(glex, new SymbolTableService(decoratedScopeCompilationUnit)));

    this.scopeClass = decorator.decorate(decoratedScopeCompilationUnit, decoratedSymbolCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedSymbolCompilationUnit);
  }

  @Test
  public void testScopeDeSerClassName(){
    assertEquals("AutomatonScopeDeSer", scopeClass.getName());
  }

  @Test
  public void testNoSuperInterfaces(){
    assertTrue(scopeClass.isEmptyInterfaces());
  }

  @Test
  public void testNoConstructors(){
    assertTrue(scopeClass.isEmptyCDConstructors());
  }

  @Test
  public void testAttributeCount(){
    assertEquals(4, scopeClass.sizeCDAttributes());
  }

  @Test
  public void testAttributes(){
    List<ASTCDAttribute> attributeList = scopeClass.getCDAttributeList();
    assertDeepEquals(CDModifier.PACKAGE_PRIVATE, attributeList.get(0).getModifier());
    assertEquals("automatonSymbolDeSer", attributeList.get(0).getName());
    assertDeepEquals(AUTOMATON_SYMBOL+DESER, attributeList.get(0).getMCType());
    assertDeepEquals(CDModifier.PACKAGE_PRIVATE, attributeList.get(1).getModifier());
    assertEquals("stateSymbolDeSer", attributeList.get(1).getName());
    assertDeepEquals(STATE_SYMBOL+DESER, attributeList.get(1).getMCType());
    assertDeepEquals(CDModifier.PACKAGE_PRIVATE, attributeList.get(2).getModifier());
    assertEquals("fooSymbolDeSer", attributeList.get(2).getName());
    assertDeepEquals(FOO_SYMBOL+DESER, attributeList.get(2).getMCType());
    assertDeepEquals(CDModifier.PRIVATE, attributeList.get(3).getModifier());
    assertEquals("symbolFileExtension", attributeList.get(3).getName());
    assertDeepEquals(String.class, attributeList.get(3).getMCType());
  }

  @Test
  public void testMethodCount(){
    assertEquals(21, scopeClass.sizeCDMethods());
  }

  @Test
  public void testGetSymbolExtensionMethod(){
    ASTCDMethod method = getMethodBy("getSymbolFileExtension", scopeClass);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals(0, method.sizeExceptions());
    assertEquals(0, method.sizeCDParameters());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());
  }

  @Test
  public void testSymbolFileExtensionMethod(){
    ASTCDMethod method = getMethodBy("setSymbolFileExtension", scopeClass);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals(0, method.sizeExceptions());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("symbolFileExtension", parameter.getName());
    assertDeepEquals(String.class, parameter.getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testLoadMethods(){
    List<ASTCDMethod> methods = getMethodsBy("load", scopeClass);
    assertEquals(3, methods.size());
    for(ASTCDMethod method: methods){
      assertDeepEquals(AUTOMATON_ARTIFACT_SCOPE, method.getMCReturnType().getMCType());
      assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
      assertEquals(1, method.sizeCDParameters());
    }

    assertEquals("url", methods.get(0).getCDParameter(0).getName());
    assertDeepEquals("java.net.URL", methods.get(0).getCDParameter(0).getMCType());

    assertEquals("reader", methods.get(1).getCDParameter(0).getName());
    assertDeepEquals("java.io.Reader", methods.get(1).getCDParameter(0).getMCType());

    assertEquals("model", methods.get(2).getCDParameter(0).getName());
    assertDeepEquals(String.class, methods.get(2).getCDParameter(0).getMCType());
  }

  @Test
  public void testLoadSymbolsIntoScopeMethod(){
    ASTCDMethod method = getMethodBy("loadSymbolsIntoScope", scopeClass);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals(0, method.sizeExceptions());
    assertEquals(3, method.sizeCDParameters());
    List<ASTCDParameter> parameters = method.getCDParameterList();
    assertEquals("qualifiedModelName", parameters.get(0).getName());
    assertDeepEquals(String.class, parameters.get(0).getMCType());
    assertEquals("enclosingScope", parameters.get(1).getName());
    assertDeepEquals(AUTOMATON_GLOBAL_SCOPE, parameters.get(1).getMCType());
    assertEquals("modelPath", parameters.get(2).getName());
    assertDeepEquals("de.monticore.io.paths.ModelPath", parameters.get(2).getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testStoreMethod(){
    ASTCDMethod method = getMethodBy("store", scopeClass);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals(0, method.sizeExceptions());
    assertEquals(2, method.sizeCDParameters());
    List<ASTCDParameter> parameters = method.getCDParameterList();
    assertEquals("toSerialize", parameters.get(0).getName());
    assertDeepEquals(AUTOMATON_ARTIFACT_SCOPE, parameters.get(0).getMCType());
    assertEquals("symbolPath", parameters.get(1).getName());
    assertDeepEquals("java.nio.file.Path", parameters.get(1).getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testSerializeMethod(){
    ASTCDMethod method = getMethodBy("serialize", scopeClass);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals(0, method.sizeExceptions());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("toSerialize", parameter.getName());
    assertDeepEquals(I_AUTOMATON_SCOPE, parameter.getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());
  }

  @Test
  public void testDeserializeMethod(){
    ASTCDMethod method = getMethodBy("deserialize", scopeClass);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals(0, method.sizeExceptions());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("serialized", parameter.getName());
    assertDeepEquals(String.class, parameter.getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(AUTOMATON_ARTIFACT_SCOPE, method.getMCReturnType().getMCType());
  }

  @Test
  public void testDeserializeAutomatonScopeMethod(){
    ASTCDMethod method = getMethodBy("deserializeAutomatonScope", scopeClass);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertEquals(0, method.sizeExceptions());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("scopeJson", parameter.getName());
    assertDeepEquals(JSON_OBJECT, parameter.getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(AUTOMATON_SCOPE, method.getMCReturnType().getMCType());
  }

  @Test
  public void testDeserializeAutomatonArtifactScopeMethod(){
    ASTCDMethod method = getMethodBy("deserializeAutomatonArtifactScope", scopeClass);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertEquals(0, method.sizeExceptions());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("scopeJson", parameter.getName());
    assertDeepEquals(JSON_OBJECT, parameter.getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(AUTOMATON_ARTIFACT_SCOPE, method.getMCReturnType().getMCType());
  }

  @Test
  public void testDeserializeAdditionalAttributesMethod(){
    ASTCDMethod method = getMethodBy("deserializeAdditionalAttributes", scopeClass);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertEquals(0, method.sizeExceptions());
    assertEquals(2, method.sizeCDParameters());
    List<ASTCDParameter> parameters = method.getCDParameterList();
    assertEquals("scope", parameters.get(0).getName());
    assertDeepEquals(I_AUTOMATON_SCOPE, parameters.get(0).getMCType());
    assertEquals("scopeJson", parameters.get(1).getName());
    assertDeepEquals(JSON_OBJECT, parameters.get(1).getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testAddSymbolsMethod(){
    ASTCDMethod method = getMethodBy("addSymbols", scopeClass);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertEquals(0, method.sizeExceptions());
    assertEquals(2, method.sizeCDParameters());
    List<ASTCDParameter> parameters = method.getCDParameterList();
    assertEquals("scopeJson", parameters.get(0).getName());
    assertDeepEquals(JSON_OBJECT, parameters.get(0).getMCType());
    assertEquals("scope", parameters.get(1).getName());
    assertDeepEquals(I_AUTOMATON_SCOPE, parameters.get(1).getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testAddAndLinkSubScopesMethod(){
    ASTCDMethod method = getMethodBy("addAndLinkSubScopes", scopeClass);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertEquals(0, method.sizeExceptions());
    assertEquals(2, method.sizeCDParameters());
    List<ASTCDParameter> parameters = method.getCDParameterList();
    assertEquals("scopeJson", parameters.get(0).getName());
    assertDeepEquals(JSON_OBJECT, parameters.get(0).getMCType());
    assertEquals("scope", parameters.get(1).getName());
    assertDeepEquals(I_AUTOMATON_SCOPE, parameters.get(1).getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testAddAndLinkSpanningSymbolMethod(){
    ASTCDMethod method = getMethodBy("addAndLinkSpanningSymbol", scopeClass);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertEquals(0, method.sizeExceptions());
    assertEquals(3, method.sizeCDParameters());
    List<ASTCDParameter> parameters = method.getCDParameterList();
    assertEquals("subScopeJson", parameters.get(0).getName());
    assertDeepEquals(JSON_OBJECT, parameters.get(0).getMCType());
    assertEquals("subScope", parameters.get(1).getName());
    assertDeepEquals(I_AUTOMATON_SCOPE, parameters.get(1).getMCType());
    assertEquals("scope", parameters.get(2).getName());
    assertDeepEquals(I_AUTOMATON_SCOPE, parameters.get(2).getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testDeserializeAutomatonSymbol(){
    ASTCDMethod method = getMethodBy("deserializeAutomatonSymbol", scopeClass);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertEquals(0, method.sizeExceptions());
    assertEquals(2, method.sizeCDParameters());
    List<ASTCDParameter> parameters = method.getCDParameterList();
    assertEquals("symbolJson", parameters.get(0).getName());
    assertDeepEquals(JSON_OBJECT, parameters.get(0).getMCType());
    assertEquals("scope", parameters.get(1).getName());
    assertDeepEquals(I_AUTOMATON_SCOPE, parameters.get(1).getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testDeserializeStateSymbol(){
    ASTCDMethod method = getMethodBy("deserializeStateSymbol", scopeClass);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertEquals(0, method.sizeExceptions());
    assertEquals(2, method.sizeCDParameters());
    List<ASTCDParameter> parameters = method.getCDParameterList();
    assertEquals("symbolJson", parameters.get(0).getName());
    assertDeepEquals(JSON_OBJECT, parameters.get(0).getMCType());
    assertEquals("scope", parameters.get(1).getName());
    assertDeepEquals(I_AUTOMATON_SCOPE, parameters.get(1).getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testDeserializeFooSymbol(){
    ASTCDMethod method = getMethodBy("deserializeFooSymbol", scopeClass);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertEquals(0, method.sizeExceptions());
    assertEquals(2, method.sizeCDParameters());
    List<ASTCDParameter> parameters = method.getCDParameterList();
    assertEquals("symbolJson", parameters.get(0).getName());
    assertDeepEquals(JSON_OBJECT, parameters.get(0).getMCType());
    assertEquals("scope", parameters.get(1).getName());
    assertDeepEquals(I_AUTOMATON_SCOPE, parameters.get(1).getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testGeneratedCode(){
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, scopeClass, scopeClass);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  }
}
