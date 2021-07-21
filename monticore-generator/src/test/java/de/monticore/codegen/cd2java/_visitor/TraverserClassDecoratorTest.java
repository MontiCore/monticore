/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cdbasis._ast.*;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.codegen.cd2java.CdUtilsPrinter;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.MCTypeFacade;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.CDModifier.*;
import static de.monticore.codegen.cd2java.DecoratorAssert.*;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getAttributeBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TraverserClassDecoratorTest extends DecoratorTestCase {

  public static final String AUTOMATON_VISITOR2 = "de.monticore.codegen.ast.automaton._visitor.AutomatonVisitor2";

  public static final String LEXICALS_VISITOR2 = "de.monticore.codegen.ast.lexicals._visitor.LexicalsVisitor2";

  public static final String LEXICALS_HANDLER = "de.monticore.codegen.ast.lexicals._visitor.LexicalsHandler";

  public static final String AUTOMATON_HANDLER = "de.monticore.codegen.ast.automaton._visitor.AutomatonHandler";

  private MCTypeFacade mcTypeFacade;

  private ASTCDClass traverserClass;

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit originalCompilationUnit;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  @Before
  public void setUp() {
    LogStub.init();
    LogStub.enableFailQuick(false);
    this.glex = new GlobalExtensionManagement();
    this.mcTypeFacade = MCTypeFacade.getInstance();

    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();

    this.glex.setGlobalValue("service", new VisitorService(decoratedCompilationUnit));
    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
    VisitorService visitorService = new VisitorService(decoratedCompilationUnit);
    SymbolTableService symbolTableService = new SymbolTableService(decoratedCompilationUnit);

    TraverserClassDecorator decorator = new TraverserClassDecorator(this.glex, visitorService, symbolTableService);
    this.traverserClass = decorator.decorate(decoratedCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testVisitorName() {
    assertEquals("AutomatonTraverserImplementation", traverserClass.getName());
  }

  @Test
  public void testAttributeCount() {
    assertEquals(6, traverserClass.getCDAttributeList().size());
  }

  @Test
  public void testMethodCount() {
    assertEquals(10, traverserClass.getCDMethodList().size());
  }

  @Test
  public void testInterfaceCount() {
    assertEquals(1, traverserClass.getInterfaceList().size());
  }

  @Test
  public void testInterface() {
    assertDeepEquals("de.monticore.codegen.ast.automaton._visitor.AutomatonTraverser", traverserClass.getCDInterfaceUsage().getInterface(0));
  }

  @Test
  public void testRealThisAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("realThis", traverserClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals("AutomatonTraverserImplementation", astcdAttribute.getMCType());
  }

  @Test
  public void testAutomatonVisitorListAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("automatonVisitorList", traverserClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertListOf(AUTOMATON_VISITOR2, astcdAttribute.getMCType());
  }

  @Test
  public void testLexicalsVisitorListAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("lexicalsVisitorList", traverserClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertListOf(LEXICALS_VISITOR2, astcdAttribute.getMCType());
  }

  @Test
  public void testAutomatonHandlerAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("automatonHandler", traverserClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertOptionalOf(AUTOMATON_HANDLER, astcdAttribute.getMCType());
  }

  @Test
  public void testLexicalsHandlerAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("lexicalsHandler", traverserClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertOptionalOf(LEXICALS_HANDLER, astcdAttribute.getMCType());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, traverserClass, traverserClass);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  }

  @Test
  public void testAdd4Automaton() {
    ASTCDMethod astcdMethod = getMethodBy("add4Automaton", traverserClass);
    assertDeepEquals(PUBLIC, astcdMethod.getModifier());
    assertTrue(astcdMethod.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, astcdMethod.sizeCDParameters());
    ASTCDParameter astcdParameter = astcdMethod.getCDParameter(0);
    assertEquals("automatonVisitor", astcdParameter.getName());
    assertDeepEquals(AUTOMATON_VISITOR2, astcdParameter.getMCType());

  }

  @Test
  public void testAdd4Lexicals() {
    ASTCDMethod astcdMethod = getMethodBy("add4Lexicals", traverserClass);
    assertDeepEquals(PUBLIC, astcdMethod.getModifier());
    assertTrue(astcdMethod.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, astcdMethod.sizeCDParameters());
    ASTCDParameter astcdParameter = astcdMethod.getCDParameter(0);
    assertEquals("lexicalsVisitor", astcdParameter.getName());
    assertDeepEquals(LEXICALS_VISITOR2, astcdParameter.getMCType());

  }

  @Test
  public void testGetAutomatonVisitorList() {
    ASTCDMethod astcdMethod = getMethodBy("getAutomatonVisitorList", traverserClass);
    assertDeepEquals(PUBLIC, astcdMethod.getModifier());
    assertTrue(astcdMethod.getMCReturnType().isPresentMCType());
    assertListOf(AUTOMATON_VISITOR2, astcdMethod.getMCReturnType().getMCType());
    assertEquals(0, astcdMethod.sizeCDParameters());

  }

  @Test
  public void testGetLexicalsVisitorList() {
    ASTCDMethod astcdMethod = getMethodBy("getLexicalsVisitorList", traverserClass);
    assertDeepEquals(PUBLIC, astcdMethod.getModifier());
    assertTrue(astcdMethod.getMCReturnType().isPresentMCType());
    assertListOf(LEXICALS_VISITOR2, astcdMethod.getMCReturnType().getMCType());
    assertEquals(0, astcdMethod.sizeCDParameters());

  }

  @Test
  public void testSetAutomaton() {
    ASTCDMethod astcdMethod = getMethodBy("setAutomatonHandler", traverserClass);
    assertDeepEquals(PUBLIC, astcdMethod.getModifier());
    assertTrue(astcdMethod.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, astcdMethod.sizeCDParameters());
    ASTCDParameter astcdParameter = astcdMethod.getCDParameter(0);
    assertEquals("automatonHandler", astcdParameter.getName());
    assertDeepEquals(AUTOMATON_HANDLER, astcdParameter.getMCType());

  }

  @Test
  public void testSetLexicals() {
    ASTCDMethod astcdMethod = getMethodBy("setLexicalsHandler", traverserClass);
    assertDeepEquals(PUBLIC, astcdMethod.getModifier());
    assertTrue(astcdMethod.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, astcdMethod.sizeCDParameters());
    ASTCDParameter astcdParameter = astcdMethod.getCDParameter(0);
    assertEquals("lexicalsHandler", astcdParameter.getName());
    assertDeepEquals(LEXICALS_HANDLER, astcdParameter.getMCType());

  }

  @Test
  public void testGetAutomatonHandler() {
    ASTCDMethod astcdMethod = getMethodBy("getAutomatonHandler", traverserClass);
    assertDeepEquals(PUBLIC, astcdMethod.getModifier());
    assertTrue(astcdMethod.getMCReturnType().isPresentMCType());
    assertOptionalOf(AUTOMATON_HANDLER, astcdMethod.getMCReturnType().getMCType());
    assertEquals(0, astcdMethod.sizeCDParameters());

  }

  @Test
  public void testGetLexicalsHandler() {
    ASTCDMethod astcdMethod = getMethodBy("getLexicalsHandler", traverserClass);
    assertDeepEquals(PUBLIC, astcdMethod.getModifier());
    assertTrue(astcdMethod.getMCReturnType().isPresentMCType());
    assertOptionalOf(LEXICALS_HANDLER, astcdMethod.getMCReturnType().getMCType());
    assertEquals(0, astcdMethod.sizeCDParameters());

  }

}