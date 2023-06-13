/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._od;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertBoolean;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.*;
import static org.junit.Assert.*;

public class ODDecoratorTest extends DecoratorTestCase {

  private final GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDCompilationUnit originalCompilationUnit;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDClass odClass;

  private static final String VISITOR_FULL_NAME = "de.monticore.codegen.symboltable.automaton._visitor.AutomatonVisitor2";

  private static final String TRAVERSER_FULL_NAME = "de.monticore.codegen.symboltable.automaton._visitor.AutomatonTraverser";

  private static final String INDENT_PRINTER = "de.monticore.prettyprint.IndentPrinter";

  private static final String REPORTING_REPOSITORY = "de.monticore.generating.templateengine.reporting.commons.ReportingRepository";

  private static final String AST_AUTOMATON = "de.monticore.codegen.symboltable.automaton._ast.ASTAutomaton";

  private static final String AST_STATE = "de.monticore.codegen.symboltable.automaton._ast.ASTState";

  private static final String AST_TRANSITION = "de.monticore.codegen.symboltable.automaton._ast.ASTTransition";

  private static final String AST_SCOPE = "de.monticore.codegen.symboltable.automaton._ast.ASTScope";

  private static final String AST_INHERITED_SYMBOL_CLASS = "de.monticore.codegen.symboltable.automaton._ast.ASTInheritedSymbolClass";

  private static final String AST_AUTOMATON_Node = "de.monticore.codegen.symboltable.automaton._ast.ASTAutomatonNode";

  private MCTypeFacade mcTypeFacade;

  @Before
  public void setup() {
    this.mcTypeFacade = MCTypeFacade.getInstance();

    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new VisitorService(decoratedCompilationUnit));

    this.glex.setGlobalValue("genHelper", DecorationHelper.getInstance());
    ODDecorator decorator = new ODDecorator(this.glex, new MethodDecorator(glex, new ODService(decoratedCompilationUnit)), new ODService(decoratedCompilationUnit),
        new VisitorService(decoratedCompilationUnit));
    this.odClass = decorator.decorate(decoratedCompilationUnit);
    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("service", new ODService(decoratedCompilationUnit));
    this.glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    CD4C.init(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CD2JavaTemplates.CLASS, odClass, packageDir);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testClassName() {
    assertEquals("Automaton2OD", odClass.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAttributeSize() {
    assertEquals(5, odClass.getCDAttributeList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMethodCount() {
    assertEquals(14, odClass.getCDMethodList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testNoSuperClass() {
    assertFalse(odClass.isPresentCDExtendUsage());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInterfaceCount() {
    assertEquals(2, odClass.getInterfaceList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testImplementsVisitorInterface() {
    assertDeepEquals(VISITOR_FULL_NAME, odClass.getCDInterfaceUsage().getInterface(0));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  /**
   * Attributes
   */

  @Test
  public void testTraverserAttribute() {
    ASTCDAttribute automatonVisitor = getAttributeBy("traverser", odClass);
    assertDeepEquals(PROTECTED, automatonVisitor.getModifier());
    assertDeepEquals(TRAVERSER_FULL_NAME, automatonVisitor.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testIndentPrinterAttribute() {
    ASTCDAttribute pp = getAttributeBy("pp", odClass);
    assertDeepEquals(PROTECTED, pp.getModifier());
    assertDeepEquals(INDENT_PRINTER, pp.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testReportingRepositoryAttribute() {
    ASTCDAttribute reporting = getAttributeBy("reporting", odClass);
    assertDeepEquals(PROTECTED, reporting.getModifier());
    assertDeepEquals(REPORTING_REPOSITORY, reporting.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testPrintEmptyOptionalAttribute() {
    ASTCDAttribute printEmptyOptional = getAttributeBy("printEmptyOptional", odClass);
    assertDeepEquals(PROTECTED, printEmptyOptional.getModifier());
    assertBoolean(printEmptyOptional.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testPrintEmptyListAttribute() {
    ASTCDAttribute printEmptyList = getAttributeBy("printEmptyList", odClass);
    assertDeepEquals(PROTECTED, printEmptyList.getModifier());
    assertBoolean(printEmptyList.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  /**
   * Methods
   */

  @Test
  public void testHandleMethodCount() {
    List<ASTCDMethod> methodList = getMethodsBy("handle", 1, odClass);
    assertEquals(5, methodList.size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testHandleASTAutomaton() {
    List<ASTCDMethod> methodList = getMethodsBy("handle", 1, odClass);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(AST_AUTOMATON);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testHandleASTState() {
    List<ASTCDMethod> methodList = getMethodsBy("handle", 1, odClass);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(AST_STATE);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testHandleASTTransition() {
    List<ASTCDMethod> methodList = getMethodsBy("handle", 1, odClass);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(AST_TRANSITION);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  
    assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testHandleASTScope() {
    List<ASTCDMethod> methodList = getMethodsBy("handle", 1, odClass);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(AST_SCOPE);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testHandleASTASTInheritedSymbolClass() {
    List<ASTCDMethod> methodList = getMethodsBy("handle", 1, odClass);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(AST_INHERITED_SYMBOL_CLASS);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  
    assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testPrintAttributeMethodMethod() {
    ASTCDMethod method = getMethodBy("printAttribute", odClass);
    assertDeepEquals(PROTECTED, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(2, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("name", method.getCDParameter(0).getName());
    assertDeepEquals(String.class, method.getCDParameter(1).getMCType());
    assertEquals("value", method.getCDParameter(1).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testPrintObjectMethodMethod() {
    ASTCDMethod method = getMethodBy("printObject", odClass);
    assertDeepEquals(PROTECTED, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(2, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("objName", method.getCDParameter(0).getName());
    assertDeepEquals(String.class, method.getCDParameter(1).getMCType());
    assertEquals("objType", method.getCDParameter(1).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testPrintObjectDiagramMethodMethod() {
    ASTCDMethod method = getMethodBy("printObjectDiagram", odClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());
  
    assertTrue(Log.getFindings().isEmpty());

    assertEquals(2, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("modelName", method.getCDParameter(0).getName());
    assertDeepEquals(AST_AUTOMATON_Node, method.getCDParameter(1).getMCType());
    assertEquals("node", method.getCDParameter(1).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetTraverserMethod() {
    ASTCDMethod method = getMethodBy("getTraverser", odClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(TRAVERSER_FULL_NAME, method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSetTraverserMethod() {
    ASTCDMethod method = getMethodBy("setTraverser", odClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(TRAVERSER_FULL_NAME, method.getCDParameter(0).getMCType());
    assertEquals("traverser", method.getCDParameter(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testIsPrintEmptyOptionalMethod() {
    ASTCDMethod method = getMethodBy("isPrintEmptyOptional", odClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testIsPrintEmptyListMethod() {
    ASTCDMethod method = getMethodBy("isPrintEmptyList", odClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSetPrintEmptyListMethod() {
    ASTCDMethod method = getMethodBy("setPrintEmptyList", odClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertBoolean(method.getCDParameter(0).getMCType());
    assertEquals("printEmptyList", method.getCDParameter(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSetPrintEmptyOptionalMethod() {
    ASTCDMethod method = getMethodBy("setPrintEmptyOptional", odClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertBoolean(method.getCDParameter(0).getMCType());
    assertEquals("printEmptyOptional", method.getCDParameter(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

}
