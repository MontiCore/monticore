/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.cli;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.google.common.collect.Lists;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CdUtilsPrinter;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._parser.ParserService;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.symboltable.IArtifactScope;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.CDModifier.PUBLIC_STATIC;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class RunnerDecoratorTest extends DecoratorTestCase {

  private ASTCDClass cliClass;

  private MCTypeFacade mcTypeFacade;

  private static final String AST_AUTOMATON = "de.monticore.codegen.ast.automaton._ast.ASTAutomaton";

  private final GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private static final String AUTOMATON_ARTIFACT_SCOPE = "de.monticore.codegen.ast.automaton._symboltable.IAutomatonArtifactScope";

  private static final String CLI_OPTIONS = "org.apache.commons.cli.Options";

  @Before
  public void setup() {
    LogStub.init();
    LogStub.enableFailQuick(false);
    ASTCDCompilationUnit ast = parse("de", "monticore", "codegen", "ast", "Automaton");
    this.glex.setGlobalValue("service", new AbstractService(ast));
    this.glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());

    RunnerDecorator runnerDecorator = new RunnerDecorator(glex, new ParserService(ast), new SymbolTableService(ast));
    this.cliClass = runnerDecorator.decorate(ast).get();
    this.mcTypeFacade = MCTypeFacade.getInstance();
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, cliClass, cliClass);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  }

  @Test
  public void testRunnerName() {
    assertEquals("AutomatonRunner", cliClass.getName());
  }

  @Test
  public void testMethodCount(){
   assertEquals(10, cliClass.getCDMethodList().size());
  }

  @Test
  public void testNoAttribute(){
    assertTrue(cliClass.getCDAttributeList().isEmpty());
  }

  @Test
  public void testNoConstructor(){
    assertTrue(cliClass.getCDConstructorList().isEmpty());
  }
  @Test
  public void testCreateSymbolTablerMethod() {
    ASTCDMethod method = getMethodBy("createSymbolTable", cliClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(AUTOMATON_ARTIFACT_SCOPE,method.getMCReturnType().getMCType());
    assertFalse(method.isEmptyCDParameters());
    assertEquals(1, method.getCDParameterList().size());
    assertDeepEquals(AST_AUTOMATON, method.getCDParameter(0).getMCType());
    assertEquals("node", method.getCDParameter(0).getName());
  }
  @Test
  public void testCreateParseMethod() {
    ASTCDMethod method = getMethodBy("parse", cliClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(AST_AUTOMATON, method.getMCReturnType().getMCType());
    assertFalse(method.isEmptyCDParameters());
    assertEquals(1, method.getCDParameterList().size());
    assertDeepEquals("String", method.getCDParameter(0).getMCType());
    assertEquals("model", method.getCDParameter(0).getName());
  }
  //String[] als parameter=>
  @Test
  public void testcreateRunMethod() {
    ASTCDMethod method = getMethodBy("run", cliClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.getCDParameterList().size());
    assertDeepEquals("String[]", method.getCDParameter(0).getMCType());

    assertEquals("args", method.getCDParameter(0).getName());
  }
  @Test
  public void testcreatePrettyPrintMethod() {
    ASTCDMethod method = getMethodBy("prettyPrint", cliClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(2, method.getCDParameterList().size());
    assertDeepEquals(AST_AUTOMATON , method.getCDParameter(0).getMCType());
    assertDeepEquals("String" , method.getCDParameter(1).getMCType());
    assertEquals("ast", method.getCDParameter(0).getName());
    assertEquals("file", method.getCDParameter(1).getName());

  }
  @Test
  public void testcreatePrintMethod() {
    ASTCDMethod method = getMethodBy("print", cliClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(2, method.getCDParameterList().size());
    assertDeepEquals("String" , method.getCDParameter(0).getMCType());
    assertDeepEquals("String" , method.getCDParameter(1).getMCType());
    assertEquals("content", method.getCDParameter(0).getName());
    assertEquals("path", method.getCDParameter(1).getName());
  }
//Options parameter
  @Test
  public void testcreatePrintHelpMethod() {
    ASTCDMethod method = getMethodBy("printHelp", cliClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.getCDParameterList().size());
    assertDeepEquals(CLI_OPTIONS, method.getCDParameter(0).getMCType());
    assertEquals("options", method.getCDParameter(0).getName());
  }

  @Test
  public void testcreateReportMethod() {
    ASTCDMethod method = getMethodBy("report", cliClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(2, method.getCDParameterList().size());
    assertDeepEquals(AST_AUTOMATON , method.getCDParameter(0).getMCType());
    assertDeepEquals("String" , method.getCDParameter(1).getMCType());
    assertEquals("ast", method.getCDParameter(0).getName());
    assertEquals("path", method.getCDParameter(1).getName());
  }

  @Test
  public void testRunDefaultCoCosMethod() {
    ASTCDMethod method = getMethodBy("runDefaultCoCos", cliClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.getCDParameterList().size());
    assertDeepEquals(AST_AUTOMATON, method.getCDParameter(0).getMCType());
    assertEquals("ast", method.getCDParameter(0).getName());
  }

  @Test
  public void testStoreSymbolsMethod() {
    ASTCDMethod method = getMethodBy("storeSymbols", cliClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(2, method.getCDParameterList().size());
    assertDeepEquals(AUTOMATON_ARTIFACT_SCOPE , method.getCDParameter(0).getMCType());
    assertDeepEquals("String" , method.getCDParameter(1).getMCType());
    assertEquals("scope", method.getCDParameter(0).getName());
    assertEquals("path", method.getCDParameter(1).getName());
  }
  // return type
  @Test
  public void testCreateInitOptionsMethod() {
    ASTCDMethod method = getMethodBy("initOptions", cliClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(CLI_OPTIONS, method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }
}
