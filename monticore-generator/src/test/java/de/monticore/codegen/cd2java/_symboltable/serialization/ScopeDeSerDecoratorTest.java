/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.serialization;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.MCPath;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertBoolean;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertListOf;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertOptionalOf;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodsBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ScopeDeSerDecoratorTest extends DecoratorTestCase {

  private ASTCDClass scopeClass;

  private ASTCDCompilationUnit decoratedSymbolCompilationUnit;

  private ASTCDCompilationUnit decoratedScopeCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  private static final String JSON_OBJECT = "de.monticore.symboltable.serialization.json.JsonObject";

  private static final String AUTOMATON_SCOPE = "de.monticore.codegen.symboltable.automaton._symboltable.IAutomatonScope";

  private static final String AUTOMATON_ARTIFACT_SCOPE = "de.monticore.codegen.symboltable.automaton._symboltable.IAutomatonArtifactScope";

  private static final String I_AUTOMATON_SCOPE = "de.monticore.codegen.symboltable.automaton._symboltable.IAutomatonScope";

  public static final String I_SCOPE = "de.monticore.symboltable.IScope";

  @Before
  public void setUp() {
    this.glex = new GlobalExtensionManagement();

    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
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
        new VisitorService(astcdCompilationUnit), new MCPath());

    this.scopeClass = decorator
        .decorate(decoratedScopeCompilationUnit, decoratedSymbolCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedSymbolCompilationUnit);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testScopeDeSerClassName() {
    assertEquals("AutomatonDeSer", scopeClass.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSuperInterfaceCount() {
    assertEquals(1, scopeClass.getInterfaceList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testConstructorCount() {
    assertEquals(0, scopeClass.getCDConstructorList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAttributeCount() {
    assertEquals(0, scopeClass.getCDAttributeList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMethodCount() {
    assertEquals(21, scopeClass.getCDMethodList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSerializeMethods() {
    List<ASTCDMethod> methodList = getMethodsBy("serialize", scopeClass);
    assertEquals(2, methodList.size());
    for (ASTCDMethod method : methodList) {
      assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
      assertFalse(method.isPresentCDThrowsDeclaration());
      assertEquals(2, method.sizeCDParameters(), 1);
      ASTCDParameter parameter = method.getCDParameter(0);
      assertEquals("toSerialize", parameter.getName());
      assertOneOf(parameter.getMCType(), I_AUTOMATON_SCOPE, AUTOMATON_ARTIFACT_SCOPE);
      assertFalse(method.getMCReturnType().isPresentMCVoidType());
      assertDeepEquals(String.class, method.getMCReturnType().getMCType());
  
      assertTrue(Log.getFindings().isEmpty());
    }
  }

  @Test
  public void testSerializeAddonsMethod() {
    List<ASTCDMethod> methodList = getMethodsBy("serializeAddons", scopeClass);
    assertEquals(2, methodList.size());
    for (ASTCDMethod method : methodList) {
      assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
      assertFalse(method.isPresentCDThrowsDeclaration());
      assertEquals(2, method.sizeCDParameters());
      ASTCDParameter parameter = method.getCDParameter(0);
      assertEquals("toSerialize", parameter.getName());
      assertOneOf(parameter.getMCType(), I_AUTOMATON_SCOPE, AUTOMATON_ARTIFACT_SCOPE);
      assertTrue(method.getMCReturnType().isPresentMCVoidType());
    }
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDeserializeScopeMethod() {
    ASTCDMethod method = getMethodBy("deserializeScope", scopeClass);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertFalse(method.isPresentCDThrowsDeclaration());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("scopeJson", parameter.getName());
    assertDeepEquals(JSON_OBJECT, parameter.getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(AUTOMATON_SCOPE, method.getMCReturnType().getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDeserializeArtifactScopeMethod() {
    ASTCDMethod method = getMethodBy("deserializeArtifactScope", scopeClass);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertFalse(method.isPresentCDThrowsDeclaration());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("scopeJson", parameter.getName());
    assertDeepEquals(JSON_OBJECT, parameter.getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(AUTOMATON_ARTIFACT_SCOPE, method.getMCReturnType().getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDeserializeAddonsMethods() {
    List<ASTCDMethod> methodList = getMethodsBy("deserializeAddons", scopeClass);
    assertEquals(2, methodList.size());
    for (ASTCDMethod method : methodList) {
      assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
      assertFalse(method.isPresentCDThrowsDeclaration());
      List<ASTCDParameter> parameters = method.getCDParameterList();
      assertEquals("scope", parameters.get(0).getName());
      assertOneOf(parameters.get(0).getMCType(), I_AUTOMATON_SCOPE, AUTOMATON_ARTIFACT_SCOPE);
      assertEquals("scopeJson", parameters.get(1).getName());
      assertDeepEquals(JSON_OBJECT, parameters.get(1).getMCType());
      assertTrue(method.getMCReturnType().isPresentMCVoidType());
  
      assertTrue(Log.getFindings().isEmpty());
    }
  }

  @Test
  public void testDeserializeExtraAttributeMethod(){
    List<ASTCDMethod> methods = getMethodsBy("deserializeExtraAttribute", scopeClass);
    assertEquals(3, methods.size());
    ASTCDMethod method = methods.get(0);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertFalse(method.isPresentCDThrowsDeclaration());
    assertEquals(1, method.sizeCDParameters());
    List<ASTCDParameter> parameters = method.getCDParameterList();
    assertEquals("scopeJson", parameters.get(0).getName());
    assertDeepEquals(JSON_OBJECT, parameters.get(0).getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertBoolean(method.getMCReturnType().getMCType());

    method = methods.get(1);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertFalse(method.isPresentCDThrowsDeclaration());
    assertEquals(2, method.sizeCDParameters());
    parameters = method.getCDParameterList();
    assertEquals("enclosingScope", parameters.get(0).getName());
    assertDeepEquals(I_SCOPE, parameters.get(0).getMCType());
    assertEquals("scopeJson", parameters.get(1).getName());
    assertDeepEquals(JSON_OBJECT, parameters.get(1).getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertBoolean(method.getMCReturnType().getMCType());

    method = methods.get(2);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertFalse(method.isPresentCDThrowsDeclaration());
    assertEquals(2, method.sizeCDParameters());
    parameters = method.getCDParameterList();
    assertEquals("enclosingScope", parameters.get(0).getName());
    assertDeepEquals(I_AUTOMATON_SCOPE, parameters.get(0).getMCType());
    assertEquals("scopeJson", parameters.get(1).getName());
    assertDeepEquals(JSON_OBJECT, parameters.get(1).getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertBoolean(method.getMCReturnType().getMCType());

    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDeserializeFooMethod(){
    List<ASTCDMethod> methods = getMethodsBy("deserializeFoo", scopeClass);
    assertEquals(3, methods.size());
    ASTCDMethod method = methods.get(0);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertFalse(method.isPresentCDThrowsDeclaration());
    assertEquals(1, method.sizeCDParameters());
    List<ASTCDParameter> parameters = method.getCDParameterList();
    assertEquals("scopeJson", parameters.get(0).getName());
    assertDeepEquals(JSON_OBJECT, parameters.get(0).getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertListOf(String.class, method.getMCReturnType().getMCType());

    method = methods.get(1);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertFalse(method.isPresentCDThrowsDeclaration());
    assertEquals(2, method.sizeCDParameters());
    parameters = method.getCDParameterList();
    assertEquals("enclosingScope", parameters.get(0).getName());
    assertDeepEquals(I_SCOPE, parameters.get(0).getMCType());
    assertEquals("scopeJson", parameters.get(1).getName());
    assertDeepEquals(JSON_OBJECT, parameters.get(1).getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertListOf(String.class, method.getMCReturnType().getMCType());

    method = methods.get(2);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertFalse(method.isPresentCDThrowsDeclaration());
    assertEquals(2, method.sizeCDParameters());
    parameters = method.getCDParameterList();
    assertEquals("enclosingScope", parameters.get(0).getName());
    assertDeepEquals(I_AUTOMATON_SCOPE, parameters.get(0).getMCType());
    assertEquals("scopeJson", parameters.get(1).getName());
    assertDeepEquals(JSON_OBJECT, parameters.get(1).getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertListOf(String.class, method.getMCReturnType().getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDeserializeBlaMethod(){
    List<ASTCDMethod> methods = getMethodsBy("deserializeBla", scopeClass);
    assertEquals(3, methods.size());
    ASTCDMethod method = methods.get(0);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertFalse(method.isPresentCDThrowsDeclaration());
    assertEquals(1, method.sizeCDParameters());
    List<ASTCDParameter> parameters = method.getCDParameterList();
    assertEquals("scopeJson", parameters.get(0).getName());
    assertDeepEquals(JSON_OBJECT, parameters.get(0).getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertOptionalOf(Integer.class, method.getMCReturnType().getMCType());

    method = methods.get(1);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertFalse(method.isPresentCDThrowsDeclaration());
    assertEquals(2, method.sizeCDParameters());
    parameters = method.getCDParameterList();
    assertEquals("enclosingScope", parameters.get(0).getName());
    assertDeepEquals(I_SCOPE, parameters.get(0).getMCType());
    assertEquals("scopeJson", parameters.get(1).getName());
    assertDeepEquals(JSON_OBJECT, parameters.get(1).getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertOptionalOf(Integer.class, method.getMCReturnType().getMCType());

    method = methods.get(2);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertFalse(method.isPresentCDThrowsDeclaration());
    assertEquals(2, method.sizeCDParameters());
    parameters = method.getCDParameterList();
    assertEquals("enclosingScope", parameters.get(0).getName());
    assertDeepEquals(I_AUTOMATON_SCOPE, parameters.get(0).getMCType());
    assertEquals("scopeJson", parameters.get(1).getName());
    assertDeepEquals(JSON_OBJECT, parameters.get(1).getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertOptionalOf(Integer.class, method.getMCReturnType().getMCType());

    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAddSymbolsMethod() {
    ASTCDMethod method = getMethodBy("deserializeSymbols", scopeClass);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertFalse(method.isPresentCDThrowsDeclaration());
    assertEquals(2, method.sizeCDParameters());
    List<ASTCDParameter> parameters = method.getCDParameterList();
    assertEquals("scope", parameters.get(0).getName());
    assertDeepEquals(I_AUTOMATON_SCOPE, parameters.get(0).getMCType());
    assertEquals("scopeJson", parameters.get(1).getName());
    assertDeepEquals(JSON_OBJECT, parameters.get(1).getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    CD4C.init(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CD2JavaTemplates.CLASS, scopeClass, packageDir);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  
    assertTrue(Log.getFindings().isEmpty());
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
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
