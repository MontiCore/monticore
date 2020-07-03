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
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.MCTypeFacade;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.monticore.codegen.cd2java.DecoratorAssert.*;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.*;
import static org.junit.Assert.*;

public class SymbolTablePrinterDecoratorTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex;

  private MCTypeFacade mcTypeFacade;

  private ASTCDCompilationUnit decoratedSymbolCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  private ASTCDCompilationUnit decoratedScopeCompilationUnit;

  private ASTCDClass symbolTablePrinterClass;

  private static final String JSON_PRINTER = "de.monticore.symboltable.serialization.JsonPrinter";

  private static final String AUTOMATON_VISITOR = "de.monticore.codegen.symboltable.automatonsymbolcd._visitor.AutomatonSymbolCDVisitor";

  private static final String I_AUTOMATON_SCOPE = "de.monticore.codegen.symboltable.automaton._symboltable.IAutomatonScope";

  private static final String AUTOMATON_SCOPE = "de.monticore.codegen.symboltable.automaton._symboltable.AutomatonScope";

  private static final String AUTOMATON_ARTIFACT_SCOPE = "de.monticore.codegen.symboltable.automaton._symboltable.AutomatonArtifactScope";

  private static final String AUTOMATON_SYMBOL = "de.monticore.codegen.symboltable.automaton._symboltable.AutomatonSymbol";

  private static final String STATE_SYMBOL = "de.monticore.codegen.symboltable.automaton._symboltable.StateSymbol";

  private static final String FOO_SYMBOL = "de.monticore.codegen.symboltable.automaton._symboltable.FooSymbol";

  private static final String I_SCOPE_SPANNING_SYMBOL = "de.monticore.symboltable.IScopeSpanningSymbol";

  @Before
  public void setUp(){
    Log.init();
    this.mcTypeFacade = MCTypeFacade.getInstance();
    this.glex = new GlobalExtensionManagement();

    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    ASTCDCompilationUnit astcdCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "Automaton");
    decoratedSymbolCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "AutomatonSymbolCD");
    decoratedScopeCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "AutomatonScopeCD");
    originalCompilationUnit = decoratedSymbolCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(astcdCompilationUnit));

    SymbolTablePrinterDecorator decorator = new SymbolTablePrinterDecorator(glex, new SymbolTableService(astcdCompilationUnit), new VisitorService(decoratedSymbolCompilationUnit));
    this.symbolTablePrinterClass = decorator.decorate(decoratedScopeCompilationUnit, decoratedSymbolCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedSymbolCompilationUnit);
  }

  @Test
  public void testClassName(){
    assertEquals("AutomatonSymbolTablePrinter", symbolTablePrinterClass.getName());
  }

  @Test
  public void testSuperInterfaces(){
    assertEquals(1, symbolTablePrinterClass.sizeInterfaces());
    assertDeepEquals(AUTOMATON_VISITOR, symbolTablePrinterClass.getInterface(0));
  }

  @Test
  public void testCountConstructors(){
    assertEquals(2, symbolTablePrinterClass.sizeCDConstructors());
  }

  @Test
  public void testConstructors(){
    List<ASTCDConstructor> constructors = symbolTablePrinterClass.getCDConstructorList();
    assertDeepEquals(CDModifier.PUBLIC, constructors.get(0).getModifier());
    assertTrue(constructors.get(0).isEmptyCDParameters());
    assertDeepEquals(CDModifier.PUBLIC, constructors.get(1).getModifier());
    assertEquals(1, constructors.get(1).sizeCDParameters());
    assertDeepEquals(JSON_PRINTER, constructors.get(1).getCDParameter(0).getMCType());
  }

  @Test
  public void testCountAttributes(){
    assertEquals(2, symbolTablePrinterClass.sizeCDAttributes());
  }

  @Test
  public void testJsonPrinterAttribute(){
    ASTCDAttribute attribute = getAttributeBy("printer", symbolTablePrinterClass);
    assertDeepEquals(CDModifier.PROTECTED, attribute.getModifier());
    assertDeepEquals(JSON_PRINTER, attribute.getMCType());
  }
  
  @Test
  public void testLexicalsSymbolTablePrinterDelegateAttribute(){
    ASTCDAttribute attribute = getAttributeBy("lexicalsSymbolTablePrinterDelegate", symbolTablePrinterClass);
    assertDeepEquals(CDModifier.PROTECTED, attribute.getModifier());
    assertDeepEquals("de.monticore.codegen.ast.lexicals._symboltable.LexicalsSymbolTablePrinter", attribute.getMCType());
  }

  @Test
  public void testMethodCount(){
    assertEquals(31, symbolTablePrinterClass.sizeCDMethods());
  }

  @Test
  public void testGetJsonPrinterMethod(){
    ASTCDMethod method = getMethodBy("getJsonPrinter", symbolTablePrinterClass);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals(0, method.sizeExceptions());
    assertEquals(0, method.sizeCDParameters());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(JSON_PRINTER, method.getMCReturnType().getMCType());
  }

  @Test
  public void testSetJsonPrinterMethod(){
    ASTCDMethod method = getMethodBy("setJsonPrinter", symbolTablePrinterClass);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals(0, method.sizeExceptions());
    assertEquals(1, method.sizeCDParameters());
    assertEquals("printer", method.getCDParameter(0).getName());
    assertDeepEquals(JSON_PRINTER, method.getCDParameter(0).getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testGetRealThisMethod(){
    ASTCDMethod method = getMethodBy("getRealThis", symbolTablePrinterClass);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals(0, method.sizeExceptions());
    assertEquals(0, method.sizeCDParameters());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(symbolTablePrinterClass.getName(), method.getMCReturnType().getMCType());
  }

  @Test
  public void testGetSerializedStringMethod(){
    ASTCDMethod method = getMethodBy("getSerializedString", symbolTablePrinterClass);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals(0, method.sizeExceptions());
    assertEquals(0, method.sizeCDParameters());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());
  }

  @Test
  public void testHasSymbolsInSubScopesMethod(){
    ASTCDMethod method = getMethodBy("hasSymbolsInSubScopes", symbolTablePrinterClass);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertEquals(0, method.sizeExceptions());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("scope", parameter.getName());
    assertDeepEquals(I_AUTOMATON_SCOPE, parameter.getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertBoolean(method.getMCReturnType().getMCType());
  }

  @Test
  public void testFilterRelevantSubScopesMethod(){
    ASTCDMethod method = getMethodBy("filterRelevantSubScopes", symbolTablePrinterClass);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertEquals(0, method.sizeExceptions());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("subScopes", parameter.getName());
    assertDeepEquals("List<? extends "+I_AUTOMATON_SCOPE+">", parameter.getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertListOf(I_AUTOMATON_SCOPE, method.getMCReturnType().getMCType());
  }

  @Test
  public void testAddScopeSpanningSymbolMethod(){
    ASTCDMethod method = getMethodBy("addScopeSpanningSymbol", symbolTablePrinterClass);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertEquals(0, method.sizeExceptions());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("spanningSymbol", parameter.getName());
    assertDeepEquals(I_SCOPE_SPANNING_SYMBOL, parameter.getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testSerializeRelevantSubScopesMethod(){
    ASTCDMethod method = getMethodBy("serializeRelevantSubScopes", symbolTablePrinterClass);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertEquals(0, method.sizeExceptions());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("node", parameter.getName());
    assertDeepEquals(I_AUTOMATON_SCOPE, parameter.getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testSerializeLocalSymbolsMethod(){
    ASTCDMethod method = getMethodBy("serializeLocalSymbols", symbolTablePrinterClass);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals(0, method.sizeExceptions());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("node", parameter.getName());
    assertDeepEquals(I_AUTOMATON_SCOPE, parameter.getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
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
    assertDeepEquals(AUTOMATON_SCOPE, methods.get(0).getCDParameter(0).getMCType());
    assertDeepEquals(AUTOMATON_SYMBOL, methods.get(1).getCDParameter(0).getMCType());
    assertDeepEquals(STATE_SYMBOL, methods.get(2).getCDParameter(0).getMCType());
    assertDeepEquals(FOO_SYMBOL, methods.get(3).getCDParameter(0).getMCType());
    assertDeepEquals(AUTOMATON_ARTIFACT_SCOPE, methods.get(4).getCDParameter(0).getMCType());
  }

  @Test
  public void testEndVisitMethods(){
    List<ASTCDMethod> methods = getMethodsBy("endVisit", symbolTablePrinterClass);
    assertEquals(5, methods.size());
    for(ASTCDMethod method: methods){
      assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
      assertTrue(method.getMCReturnType().isPresentMCVoidType());
      assertEquals(1, method.sizeCDParameters());
      assertEquals("node", method.getCDParameter(0).getName());
    }
    assertDeepEquals(AUTOMATON_SCOPE, methods.get(0).getCDParameter(0).getMCType());
    assertDeepEquals(AUTOMATON_SYMBOL, methods.get(1).getCDParameter(0).getMCType());
    assertDeepEquals(STATE_SYMBOL, methods.get(2).getCDParameter(0).getMCType());
    assertDeepEquals(FOO_SYMBOL, methods.get(3).getCDParameter(0).getMCType());
    assertDeepEquals(AUTOMATON_ARTIFACT_SCOPE, methods.get(4).getCDParameter(0).getMCType());
  }

  @Test
  public void testTraverseMethods(){
    List<ASTCDMethod> methods = getMethodsBy("traverse", symbolTablePrinterClass);
    assertEquals(2, methods.size());
    for(ASTCDMethod method: methods){
      assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
      assertTrue(method.getMCReturnType().isPresentMCVoidType());
      assertEquals(1, method.sizeCDParameters());
      assertEquals("node", method.getCDParameter(0).getName());
    }
    assertDeepEquals(AUTOMATON_SCOPE, methods.get(0).getCDParameter(0).getMCType());
    assertDeepEquals(AUTOMATON_ARTIFACT_SCOPE, methods.get(1).getCDParameter(0).getMCType());
  }

  @Test
  public void testSerializeAutomatonMethod(){
    ASTCDMethod method = getMethodBy("serializeAutomaton", symbolTablePrinterClass);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertEquals(0, method.sizeExceptions());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("node", parameter.getName());
    assertDeepEquals(AUTOMATON_SYMBOL, parameter.getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testSerializeFooMethod(){
    ASTCDMethod method = getMethodBy("serializeFoo", symbolTablePrinterClass);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertEquals(0, method.sizeExceptions());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("node", parameter.getName());
    assertDeepEquals(FOO_SYMBOL, parameter.getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testSerializeStateMethod(){
    ASTCDMethod method = getMethodBy("serializeState", symbolTablePrinterClass);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertEquals(0, method.sizeExceptions());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("node", parameter.getName());
    assertDeepEquals(STATE_SYMBOL, parameter.getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testSerializeAdditionalScopeAttributesMethod(){
    ASTCDMethod method = getMethodBy("serializeAdditionalScopeAttributes", symbolTablePrinterClass);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertEquals(0, method.sizeExceptions());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("node", parameter.getName());
    assertDeepEquals(AUTOMATON_SCOPE, parameter.getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testSerializeFooExtraAttributeMethod(){
    ASTCDMethod method = getMethodBy("serializeFooExtraAttribute", symbolTablePrinterClass);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals(0, method.sizeExceptions());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("extraAttribute", parameter.getName());
    assertBoolean(parameter.getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testSerializeFooFooMethod(){
    ASTCDMethod method = getMethodBy("serializeFooFoo", symbolTablePrinterClass);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals(0, method.sizeExceptions());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("foo", parameter.getName());
    assertListOf(String.class, parameter.getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testSerializeFooBlaMethod(){
    ASTCDMethod method = getMethodBy("serializeFooBla", symbolTablePrinterClass);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals(0, method.sizeExceptions());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("bla", parameter.getName());
    assertOptionalOf(Integer.class, parameter.getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testSerializeAutomatonScopeExtraAttributeMethod(){
    ASTCDMethod method = getMethodBy("serializeAutomatonScopeExtraAttribute", symbolTablePrinterClass);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals(0, method.sizeExceptions());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("extraAttribute", parameter.getName());
    assertBoolean(parameter.getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testSerializeAutomatonScopeFooMethod(){
    ASTCDMethod method = getMethodBy("serializeAutomatonScopeFoo", symbolTablePrinterClass);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals(0, method.sizeExceptions());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("foo", parameter.getName());
    assertListOf(String.class, parameter.getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testSerializeAutomatonScopeBlaMethod(){
    ASTCDMethod method = getMethodBy("serializeAutomatonScopeBla", symbolTablePrinterClass);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals(0, method.sizeExceptions());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("bla", parameter.getName());
    assertOptionalOf(Integer.class, parameter.getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testGeneratedCode(){
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, symbolTablePrinterClass, symbolTablePrinterClass);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  }
}
