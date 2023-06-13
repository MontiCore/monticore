/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.serialization;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cdbasis._ast.*;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd4codebasis._ast.*;
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
import de.monticore.io.paths.MCPath;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import de.se_rwth.commons.logging.Log;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.*;
import static org.junit.Assert.*;

public class Symbols2JsonDecoratorTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit decoratedSymbolCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  private ASTCDCompilationUnit decoratedScopeCompilationUnit;

  private ASTCDClass symbolTablePrinterClass;

  private static final String JSON_PRINTER = "de.monticore.symboltable.serialization.JsonPrinter";

  private static final String AUTOMATON_VISITOR = "de.monticore.codegen.symboltable.automatonsymbolcd._visitor.AutomatonSymbolCDVisitor2";

  private static final String AUTOMATON_TRAVERSER = "de.monticore.codegen.symboltable.automatonsymbolcd._visitor.AutomatonSymbolCDTraverser";

  private static final String I_AUTOMATON_SCOPE = "de.monticore.codegen.symboltable.automaton._symboltable.IAutomatonScope";

  private static final String I_AUTOMATON_ARTIFACT_SCOPE = "de.monticore.codegen.symboltable.automaton._symboltable.IAutomatonArtifactScope";

  private static final String SYMBOLS2JSON_SIMPLE = "AutomatonSymbols2Json";

  private static final String AUTOMATON_SYMBOL = "de.monticore.codegen.symboltable.automaton._symboltable.AutomatonSymbol";

  private static final String STATE_SYMBOL = "de.monticore.codegen.symboltable.automaton._symboltable.StateSymbol";

  private static final String FOO_SYMBOL = "de.monticore.codegen.symboltable.automaton._symboltable.FooSymbol";

  @Before
  public void setUp(){
    this.glex = new GlobalExtensionManagement();

    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
    ASTCDCompilationUnit astcdCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "Automaton");
    decoratedSymbolCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "AutomatonSymbolCD");
    decoratedScopeCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "AutomatonScopeCD");
    originalCompilationUnit = decoratedSymbolCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(astcdCompilationUnit));

    Symbols2JsonDecorator decorator = new Symbols2JsonDecorator(glex, new SymbolTableService(astcdCompilationUnit),
            new VisitorService(decoratedSymbolCompilationUnit),
            new MethodDecorator(glex, new SymbolTableService(decoratedSymbolCompilationUnit)),
            new MCPath());
    this.symbolTablePrinterClass = decorator.decorate(decoratedScopeCompilationUnit, decoratedSymbolCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedSymbolCompilationUnit);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testClassName(){
    assertEquals("AutomatonSymbols2Json", symbolTablePrinterClass.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSuperInterfaces(){
    assertEquals(1, symbolTablePrinterClass.getInterfaceList().size());
    assertDeepEquals(AUTOMATON_VISITOR, symbolTablePrinterClass.getInterfaceList().get(0));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCountConstructors(){
    assertEquals(2, symbolTablePrinterClass.getCDConstructorList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testConstructors(){
    List<ASTCDConstructor> constructors = symbolTablePrinterClass.getCDConstructorList();
    assertDeepEquals(CDModifier.PUBLIC, constructors.get(0).getModifier());
    assertTrue(constructors.get(0).isEmptyCDParameters());
    assertDeepEquals(CDModifier.PUBLIC, constructors.get(1).getModifier());
    assertEquals(2, constructors.get(1).sizeCDParameters());
    assertDeepEquals(AUTOMATON_TRAVERSER, constructors.get(1).getCDParameter(0).getMCType());
    assertDeepEquals(JSON_PRINTER, constructors.get(1).getCDParameter(1).getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCountAttributes(){
    assertEquals(7, symbolTablePrinterClass.getCDAttributeList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testJsonPrinterAttribute(){
    ASTCDAttribute attribute = getAttributeBy("printer", symbolTablePrinterClass);
    assertDeepEquals(CDModifier.PROTECTED, attribute.getModifier());
    assertDeepEquals(JSON_PRINTER, attribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testRealThisAttribute(){
    ASTCDAttribute attribute = getAttributeBy("realThis", symbolTablePrinterClass);
    assertDeepEquals(CDModifier.PROTECTED, attribute.getModifier());
    assertDeepEquals(SYMBOLS2JSON_SIMPLE, attribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testTravererAttribute(){
    ASTCDAttribute attribute = getAttributeBy("traverser", symbolTablePrinterClass);
    assertDeepEquals(CDModifier.PROTECTED, attribute.getModifier());
    assertDeepEquals(AUTOMATON_TRAVERSER, attribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMethodCount(){
    assertEquals(22, symbolTablePrinterClass.getCDMethodList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetJsonPrinterMethod(){
    ASTCDMethod method = getMethodBy("getJsonPrinter", symbolTablePrinterClass);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertFalse(method.isPresentCDThrowsDeclaration());
    assertEquals(0, method.sizeCDParameters());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(JSON_PRINTER, method.getMCReturnType().getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSetJsonPrinterMethod(){
    ASTCDMethod method = getMethodBy("setJsonPrinter", symbolTablePrinterClass);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertFalse(method.isPresentCDThrowsDeclaration());
    assertEquals(1, method.sizeCDParameters());
    assertEquals("printer", method.getCDParameter(0).getName());
    assertDeepEquals(JSON_PRINTER, method.getCDParameter(0).getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetTraverserMethod(){
    ASTCDMethod method = getMethodBy("getTraverser", symbolTablePrinterClass);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertFalse(method.isPresentCDThrowsDeclaration());
    assertEquals(0, method.sizeCDParameters());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(AUTOMATON_TRAVERSER, method.getMCReturnType().getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSetTraverserMethod(){
    ASTCDMethod method = getMethodBy("setTraverser", symbolTablePrinterClass);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertFalse(method.isPresentCDThrowsDeclaration());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("traverser", parameter.getName());
    assertDeepEquals(AUTOMATON_TRAVERSER, parameter.getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetSerializedStringMethod(){
    ASTCDMethod method = getMethodBy("getSerializedString", symbolTablePrinterClass);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertFalse(method.isPresentCDThrowsDeclaration());
    assertEquals(0, method.sizeCDParameters());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testVisitMethods(){
    List<ASTCDMethod> methods = getMethodsBy("visit", symbolTablePrinterClass);
    assertEquals(5, methods.size());
    for(ASTCDMethod method: methods){
      assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
      assertTrue(method.getMCReturnType().isPresentMCVoidType());
      assertEquals(1, method.sizeCDParameters());
      assertEquals("node", method.getCDParameter(0).getName());
    }
    assertDeepEquals(I_AUTOMATON_SCOPE, methods.get(0).getCDParameter(0).getMCType());
    assertDeepEquals(AUTOMATON_SYMBOL, methods.get(1).getCDParameter(0).getMCType());
    assertDeepEquals(STATE_SYMBOL, methods.get(2).getCDParameter(0).getMCType());
    assertDeepEquals(FOO_SYMBOL, methods.get(3).getCDParameter(0).getMCType());
    assertDeepEquals(I_AUTOMATON_ARTIFACT_SCOPE, methods.get(4).getCDParameter(0).getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testEndVisitMethods(){
    List<ASTCDMethod> methods = getMethodsBy("endVisit", symbolTablePrinterClass);
    assertEquals(2, methods.size());
    for(ASTCDMethod method: methods){
      assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
      assertTrue(method.getMCReturnType().isPresentMCVoidType());
      assertEquals(1, method.sizeCDParameters());
      assertEquals("node", method.getCDParameter(0).getName());
    }
    assertDeepEquals(I_AUTOMATON_SCOPE, methods.get(0).getCDParameter(0).getMCType());
    assertDeepEquals(I_AUTOMATON_ARTIFACT_SCOPE, methods.get(1).getCDParameter(0).getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInitMethods(){
    List<ASTCDMethod> methods = getMethodsBy("init", symbolTablePrinterClass);
    assertEquals(1, methods.size());
    for(ASTCDMethod method: methods){
      assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
      assertTrue(method.getMCReturnType().isPresentMCVoidType());
      assertEquals(0, method.sizeCDParameters());
    }
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testLoadMethods(){
    List<ASTCDMethod> methods = getMethodsBy("load", symbolTablePrinterClass);
    assertEquals(3, methods.size());
    for(ASTCDMethod method: methods){
      assertDeepEquals(I_AUTOMATON_ARTIFACT_SCOPE, method.getMCReturnType().getMCType());
      assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
      assertEquals(1, method.sizeCDParameters());
    }

    assertEquals("url", methods.get(0).getCDParameter(0).getName());
    assertDeepEquals("java.net.URL", methods.get(0).getCDParameter(0).getMCType());

    assertEquals("reader", methods.get(1).getCDParameter(0).getName());
    assertDeepEquals("java.io.Reader", methods.get(1).getCDParameter(0).getMCType());

    assertEquals("model", methods.get(2).getCDParameter(0).getName());
    assertDeepEquals(String.class, methods.get(2).getCDParameter(0).getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testStoreMethod(){
    ASTCDMethod method = getMethodBy("store", symbolTablePrinterClass);

    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals(2, method.sizeCDParameters());
    assertEquals("scope", method.getCDParameter(0).getName());
    assertDeepEquals(I_AUTOMATON_ARTIFACT_SCOPE, method.getCDParameter(0).getMCType());
    assertEquals("fileName", method.getCDParameter(1).getName());
    assertDeepEquals(String.class, method.getCDParameter(1).getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSerializeMethods() {
    List<ASTCDMethod> methodList = getMethodsBy("serialize", symbolTablePrinterClass);
    assertEquals(2, methodList.size());
    for (ASTCDMethod method : methodList) {
      assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
      assertFalse(method.isPresentCDThrowsDeclaration());
      assertEquals(1, method.sizeCDParameters());
      ASTCDParameter parameter = method.getCDParameter(0);
      assertEquals("toSerialize", parameter.getName());
      assertOneOf(parameter.getMCType(), I_AUTOMATON_SCOPE, I_AUTOMATON_ARTIFACT_SCOPE);
      assertFalse(method.getMCReturnType().isPresentMCVoidType());
      assertDeepEquals(String.class, method.getMCReturnType().getMCType());
    }
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDeserializeMethod(){
    ASTCDMethod method = getMethodBy("deserialize", symbolTablePrinterClass);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertFalse(method.isPresentCDThrowsDeclaration());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("serialized", parameter.getName());
    assertDeepEquals("String", parameter.getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(I_AUTOMATON_ARTIFACT_SCOPE, method.getMCReturnType().getMCType());
  
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

  @Test
  public void testGeneratedCode() {
    // Given
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);

    // When
    CD4C.init(generatorSetup);
    StringBuilder generate = generatorEngine.generate(CD2JavaTemplates.CLASS, symbolTablePrinterClass, packageDir);

    // Then
    ParseResult<CompilationUnit> parseResult = new JavaParser(new ParserConfiguration()).parse(generate.toString());
    assertTrue("Parsing of the generated code failed. The generated code is: \n"
        + generate, parseResult.isSuccessful());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
