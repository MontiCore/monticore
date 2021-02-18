/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.serialization;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.*;
import de.monticore.codegen.cd2java.CDModifier;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.IterablePath;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.monticore.codegen.cd2java.DecoratorAssert.*;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodsBy;
import static org.junit.Assert.*;

public class ScopeDeSerDecoratorTest extends DecoratorTestCase {

  private ASTCDClass scopeClass;

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit decoratedSymbolCompilationUnit;

  private ASTCDCompilationUnit decoratedScopeCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  private static final String JSON_OBJECT = "de.monticore.symboltable.serialization.json.JsonObject";

  private static final String AUTOMATON_SCOPE = "de.monticore.codegen.symboltable.automaton._symboltable.IAutomatonScope";

  private static final String AUTOMATON_ARTIFACT_SCOPE = "de.monticore.codegen.symboltable.automaton._symboltable.IAutomatonArtifactScope";

  private static final String I_AUTOMATON_SCOPE = "de.monticore.codegen.symboltable.automaton._symboltable.IAutomatonScope";



  @Before
  public void setUp() {
    this.glex = new GlobalExtensionManagement();

    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CD4CodeFullPrettyPrinter());
    ASTCDCompilationUnit astcdCompilationUnit = this
        .parse("de", "monticore", "codegen", "symboltable", "Automaton");
    decoratedSymbolCompilationUnit = this
        .parse("de", "monticore", "codegen", "symboltable", "AutomatonSymbolCD");
    decoratedScopeCompilationUnit = this
        .parse("de", "monticore", "codegen", "symboltable", "AutomatonScopeCD");
    originalCompilationUnit = decoratedSymbolCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(astcdCompilationUnit));

    ScopeDeSerDecorator decorator = new ScopeDeSerDecorator(glex,
        new SymbolTableService(astcdCompilationUnit),
        new MethodDecorator(glex, new SymbolTableService(decoratedScopeCompilationUnit)),
        new VisitorService(astcdCompilationUnit), IterablePath.empty());

    this.scopeClass = decorator
        .decorate(decoratedScopeCompilationUnit, decoratedSymbolCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedSymbolCompilationUnit);
  }

  @Test
  public void testScopeDeSerClassName() {
    assertEquals("AutomatonDeSer", scopeClass.getName());
  }

  @Test
  public void testSuperInterfaceCount() {
    assertEquals(1, scopeClass.getInterfaceList().size());
  }

  @Test
  public void testConstructorCount() {
    assertEquals(0, scopeClass.getCDConstructorList().size());
  }

  @Test
  public void testAttributeCount() {
    assertEquals(0, scopeClass.getCDAttributeList().size());
  }

  @Test
  public void testMethodCount() {
    assertEquals(18, scopeClass.getCDMethodList().size());
  }

  @Test
  public void testSerializeMethods() {
    List<ASTCDMethod> methodList = getMethodsBy("serialize", scopeClass);
    assertEquals(4, methodList.size());
    for (ASTCDMethod method : methodList) {
      assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
      assertEquals(0, method.getCDThrowsDeclaration().sizeException());
      assertEquals(2, method.sizeCDParameters(), 1);
      ASTCDParameter parameter = method.getCDParameter(0);
      assertEquals("toSerialize", parameter.getName());
      assertOneOf(parameter.getMCType(), I_AUTOMATON_SCOPE, AUTOMATON_ARTIFACT_SCOPE);
      assertFalse(method.getMCReturnType().isPresentMCVoidType());
      assertDeepEquals(String.class, method.getMCReturnType().getMCType());
    }
  }

  @Test
  public void testSerializeAddonsMethod() {
    List<ASTCDMethod> methodList = getMethodsBy("serializeAddons", scopeClass);
    assertEquals(2, methodList.size());
    for (ASTCDMethod method : methodList) {
      assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
      assertEquals(0, method.getCDThrowsDeclaration().sizeException());
      assertEquals(2, method.sizeCDParameters());
      ASTCDParameter parameter = method.getCDParameter(0);
      assertEquals("toSerialize", parameter.getName());
      assertOneOf(parameter.getMCType(), I_AUTOMATON_SCOPE, AUTOMATON_ARTIFACT_SCOPE);
      assertTrue(method.getMCReturnType().isPresentMCVoidType());
    }
  }

  @Test
  public void testDeserializeMethod() {
    ASTCDMethod method = getMethodBy("deserialize", scopeClass);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals(0, method.getCDThrowsDeclaration().sizeException());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("serialized", parameter.getName());
    assertDeepEquals(String.class, parameter.getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(AUTOMATON_ARTIFACT_SCOPE, method.getMCReturnType().getMCType());
  }

  @Test
  public void testDeserializeScopeMethod() {
    ASTCDMethod method = getMethodBy("deserializeScope", scopeClass);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals(0, method.getCDThrowsDeclaration().sizeException());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("scopeJson", parameter.getName());
    assertDeepEquals(JSON_OBJECT, parameter.getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(AUTOMATON_SCOPE, method.getMCReturnType().getMCType());
  }

  @Test
  public void testDeserializeArtifactScopeMethod() {
    ASTCDMethod method = getMethodBy("deserializeArtifactScope", scopeClass);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals(0, method.getCDThrowsDeclaration().sizeException());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("scopeJson", parameter.getName());
    assertDeepEquals(JSON_OBJECT, parameter.getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(AUTOMATON_ARTIFACT_SCOPE, method.getMCReturnType().getMCType());
  }

  @Test
  public void testDeserializeAddonsMethods() {
    List<ASTCDMethod> methodList = getMethodsBy("deserializeAddons", scopeClass);
    assertEquals(2, methodList.size());
    for (ASTCDMethod method : methodList) {
      assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
      assertEquals(0, method.getCDThrowsDeclaration().sizeException());
      List<ASTCDParameter> parameters = method.getCDParameterList();
      assertEquals("scope", parameters.get(0).getName());
      assertOneOf(parameters.get(0).getMCType(), I_AUTOMATON_SCOPE, AUTOMATON_ARTIFACT_SCOPE);
      assertEquals("scopeJson", parameters.get(1).getName());
      assertDeepEquals(JSON_OBJECT, parameters.get(1).getMCType());
      assertTrue(method.getMCReturnType().isPresentMCVoidType());
    }
  }

  @Test
  public void testDeserializeExtraAttributeMethod(){
    ASTCDMethod method = getMethodBy("deserializeExtraAttribute", scopeClass);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertEquals(0, method.getCDThrowsDeclaration().sizeException());
    assertEquals(1, method.sizeCDParameters());
    List<ASTCDParameter> parameters = method.getCDParameterList();
    assertEquals("scopeJson", parameters.get(0).getName());
    assertDeepEquals(JSON_OBJECT, parameters.get(0).getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertBoolean(method.getMCReturnType().getMCType());
  }

  @Test
  public void testDeserializeFooMethod(){
    ASTCDMethod method = getMethodBy("deserializeFoo", scopeClass);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertEquals(0, method.getCDThrowsDeclaration().sizeException());
    assertEquals(1, method.sizeCDParameters());
    List<ASTCDParameter> parameters = method.getCDParameterList();
    assertEquals("scopeJson", parameters.get(0).getName());
    assertDeepEquals(JSON_OBJECT, parameters.get(0).getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertListOf(String.class, method.getMCReturnType().getMCType());
  }

  @Test
  public void testDeserializeBlaMethod(){
    ASTCDMethod method = getMethodBy("deserializeBla", scopeClass);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertEquals(0, method.getCDThrowsDeclaration().sizeException());
    assertEquals(1, method.sizeCDParameters());
    List<ASTCDParameter> parameters = method.getCDParameterList();
    assertEquals("scopeJson", parameters.get(0).getName());
    assertDeepEquals(JSON_OBJECT, parameters.get(0).getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertOptionalOf(Integer.class, method.getMCReturnType().getMCType());
  }

  @Test
  public void testAddSymbolsMethod() {
    ASTCDMethod method = getMethodBy("deserializeSymbols", scopeClass);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertEquals(0, method.getCDThrowsDeclaration().sizeException());
    assertEquals(2, method.sizeCDParameters());
    List<ASTCDParameter> parameters = method.getCDParameterList();
    assertEquals("scope", parameters.get(0).getName());
    assertDeepEquals(I_AUTOMATON_SCOPE, parameters.get(0).getMCType());
    assertEquals("scopeJson", parameters.get(1).getName());
    assertDeepEquals(JSON_OBJECT, parameters.get(1).getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testGeneratedCode() {
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

  public static void assertOneOf(ASTMCType actualType, String... expected) {
    boolean result = false;
    String actual = actualType.printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter()));
    for (String exp : expected) {
      if (actual.equals(exp)) {
        result = true;
      }
    }
    if (!result) {
      fail();
    }
  }
}
