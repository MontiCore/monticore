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
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.monticore.codegen.cd2java.DecoratorAssert.*;
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

  private static final String AUTOMATON_SYMBOL = "de.monticore.codegen.symboltable.automaton._symboltable.AutomatonSymbol";

  private static final String STATE_SYMBOL = "de.monticore.codegen.symboltable.automaton._symboltable.StateSymbol";

  private static final String FOO_SYMBOL = "de.monticore.codegen.symboltable.automaton._symboltable.FooSymbol";

  @Before
  public void setUp(){
    Log.init();
    this.glex = new GlobalExtensionManagement();

    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    ASTCDCompilationUnit astcdCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "Automaton");
    decoratedSymbolCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "AutomatonSymbolCD");
    decoratedScopeCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "AutomatonScopeCD");
    originalCompilationUnit = decoratedSymbolCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(astcdCompilationUnit));

    Symbols2JsonDecorator decorator = new Symbols2JsonDecorator(glex, new SymbolTableService(astcdCompilationUnit),
            new VisitorService(decoratedSymbolCompilationUnit),
            new MethodDecorator(glex, new SymbolTableService(decoratedSymbolCompilationUnit)));
    this.symbolTablePrinterClass = decorator.decorate(decoratedScopeCompilationUnit, decoratedSymbolCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedSymbolCompilationUnit);
  }

  @Test
  public void testClassName(){
    assertEquals("AutomatonSymbols2Json", symbolTablePrinterClass.getName());
  }

  @Test
  public void testSuperInterfaces(){
    assertEquals(1, symbolTablePrinterClass.sizeInterface());
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
    assertEquals(2, constructors.get(1).sizeCDParameters());
    assertDeepEquals(AUTOMATON_TRAVERSER, constructors.get(1).getCDParameter(0).getMCType());
    assertDeepEquals(JSON_PRINTER, constructors.get(1).getCDParameter(1).getMCType());
  }

  @Test
  public void testCountAttributes(){
    assertEquals(6, symbolTablePrinterClass.sizeCDAttributes());
  }

  @Test
  public void testJsonPrinterAttribute(){
    ASTCDAttribute attribute = getAttributeBy("printer", symbolTablePrinterClass);
    assertDeepEquals(CDModifier.PROTECTED, attribute.getModifier());
    assertDeepEquals(JSON_PRINTER, attribute.getMCType());
  }

  @Test
  public void testTravererAttribute(){
    ASTCDAttribute attribute = getAttributeBy("traverser", symbolTablePrinterClass);
    assertDeepEquals(CDModifier.PRIVATE, attribute.getModifier());
    assertDeepEquals(AUTOMATON_TRAVERSER, attribute.getMCType());
  }

  @Test
  public void testMethodCount(){
    assertEquals(30, symbolTablePrinterClass.sizeCDMethods());
  }

  @Test
  public void testGetJsonPrinterMethod(){
    ASTCDMethod method = getMethodBy("getJsonPrinter", symbolTablePrinterClass);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals(0, method.sizeException());
    assertEquals(0, method.sizeCDParameters());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(JSON_PRINTER, method.getMCReturnType().getMCType());
  }

  @Test
  public void testPrintKindHierarchyMethod(){
    ASTCDMethod method = getMethodBy("printKindHierarchy", symbolTablePrinterClass);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals(0, method.sizeException());
    assertEquals(0, method.sizeCDParameters());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testSetJsonPrinterMethod(){
    ASTCDMethod method = getMethodBy("setJsonPrinter", symbolTablePrinterClass);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals(0, method.sizeException());
    assertEquals(1, method.sizeCDParameters());
    assertEquals("printer", method.getCDParameter(0).getName());
    assertDeepEquals(JSON_PRINTER, method.getCDParameter(0).getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testGetTraverserMethod(){
    ASTCDMethod method = getMethodBy("getTraverser", symbolTablePrinterClass);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals(0, method.sizeException());
    assertEquals(0, method.sizeCDParameters());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(AUTOMATON_TRAVERSER, method.getMCReturnType().getMCType());
  }

  @Test
  public void testSetTraverserMethod(){
    ASTCDMethod method = getMethodBy("setTraverser", symbolTablePrinterClass);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals(0, method.sizeException());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("traverser", parameter.getName());
    assertDeepEquals(AUTOMATON_TRAVERSER, parameter.getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testGetSerializedStringMethod(){
    ASTCDMethod method = getMethodBy("getSerializedString", symbolTablePrinterClass);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals(0, method.sizeException());
    assertEquals(0, method.sizeCDParameters());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());
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
  }

  @Test
  public void testSerializeAdditionalScopeAttributesMethod(){
    ASTCDMethod method = getMethodBy("serializeAdditionalScopeAttributes", symbolTablePrinterClass);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertEquals(0, method.sizeException());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("node", parameter.getName());
    assertDeepEquals(I_AUTOMATON_SCOPE, parameter.getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testSerializeAdditionalArtifactScopeAttributesMethod(){
    ASTCDMethod method = getMethodBy("serializeAdditionalArtifactScopeAttributes", symbolTablePrinterClass);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertEquals(0, method.sizeException());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("node", parameter.getName());
    assertDeepEquals(I_AUTOMATON_ARTIFACT_SCOPE, parameter.getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testSerializeFooExtraAttributeMethod(){
    ASTCDMethod method = getMethodBy("serializeFooExtraAttribute", symbolTablePrinterClass);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals(0, method.sizeException());
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
    assertEquals(0, method.sizeException());
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
    assertEquals(0, method.sizeException());
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
    assertEquals(0, method.sizeException());
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
    assertEquals(0, method.sizeException());
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
    assertEquals(0, method.sizeException());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("bla", parameter.getName());
    assertOptionalOf(Integer.class, parameter.getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testSerializeAdditionalAutomatonSymbolAttributesMethod(){
    ASTCDMethod method = getMethodBy("serializeAdditionalAutomatonSymbolAttributes", symbolTablePrinterClass);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertEquals(0, method.sizeException());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("node", parameter.getName());
    assertDeepEquals(AUTOMATON_SYMBOL, parameter.getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testSerializeAdditionalStateSymbolAttributesMethod(){
    ASTCDMethod method = getMethodBy("serializeAdditionalStateSymbolAttributes", symbolTablePrinterClass);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertEquals(0, method.sizeException());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("node", parameter.getName());
    assertDeepEquals(STATE_SYMBOL, parameter.getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testSerializeAdditionalFooSymbolAttributesMethod(){
    ASTCDMethod method = getMethodBy("serializeAdditionalFooSymbolAttributes", symbolTablePrinterClass);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertEquals(0, method.sizeException());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("node", parameter.getName());
    assertDeepEquals(FOO_SYMBOL, parameter.getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
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
