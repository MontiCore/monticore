/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.mill;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.*;
import de.monticore.codegen.cd2java._visitor.builder.DelegatorVisitorBuilderDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.IterablePath;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static de.monticore.cd.facade.CDModifier.*;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MillDecoratorTest extends DecoratorTestCase {

  private ASTCDClass millClass;

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit originalCompilationUnit;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  @Before
  public void setUp() {
    LogStub.init();
    LogStub.enableFailQuick(false);
    this.glex = new GlobalExtensionManagement();

    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new VisitorService(decoratedCompilationUnit));

    ASTService astService = new ASTService(decoratedCompilationUnit);

    MillDecorator decorator = new MillDecorator(this.glex, astService);
    this.millClass = decorator.decorate(Lists.newArrayList(decoratedCompilationUnit, getVisitorCD()));
  }

  protected ASTCDCompilationUnit getVisitorCD(){
    IterablePath targetPath = Mockito.mock(IterablePath.class);
    VisitorService visitorService = new VisitorService(decoratedCompilationUnit);
    SymbolTableService symbolTableService = new SymbolTableService(decoratedCompilationUnit);

    VisitorDecorator astVisitorDecorator = new VisitorDecorator(this.glex, visitorService, symbolTableService);
    DelegatorVisitorDecorator delegatorVisitorDecorator = new DelegatorVisitorDecorator(this.glex, visitorService, symbolTableService);
    ParentAwareVisitorDecorator parentAwareVisitorDecorator = new ParentAwareVisitorDecorator(this.glex, visitorService);
    InheritanceVisitorDecorator inheritanceVisitorDecorator = new InheritanceVisitorDecorator(this.glex, visitorService, symbolTableService);
    DelegatorVisitorBuilderDecorator delegatorVisitorBuilderDecorator = new DelegatorVisitorBuilderDecorator(this.glex, visitorService, symbolTableService);


    CDVisitorDecorator decorator = new CDVisitorDecorator(this.glex, targetPath, visitorService,
        astVisitorDecorator, delegatorVisitorDecorator, inheritanceVisitorDecorator,
        parentAwareVisitorDecorator, delegatorVisitorBuilderDecorator);
    return decorator.decorate(decoratedCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testMillName() {
    assertEquals("AutomatonMill", millClass.getName());
  }

  @Test
  public void testAttributeName() {
    assertEquals("mill", millClass.getCDAttribute(0).getName());
    assertEquals("millASTAutomaton", millClass.getCDAttribute(1).getName());
    assertEquals("millASTState", millClass.getCDAttribute(2).getName());
    assertEquals("millASTTransition", millClass.getCDAttribute(3).getName());
    assertEquals("millAutomatonDelegatorVisitor", millClass.getCDAttribute(4).getName());
  }

  @Test
  public void testAttributeModifier() {
    for (ASTCDAttribute astcdAttribute : millClass.getCDAttributeList()) {
      assertTrue(astcdAttribute.isPresentModifier());
      assertTrue(PROTECTED_STATIC.build().deepEquals(astcdAttribute.getModifier()));
    }
  }

  @Test
  public void testConstructor() {
    assertEquals(1, millClass.sizeCDConstructors());
    assertTrue(PROTECTED.build().deepEquals(millClass.getCDConstructor(0).getModifier()));
    assertEquals("AutomatonMill", millClass.getCDConstructor(0).getName());
  }

  @Test
  public void testGetMillMethod() {
    ASTCDMethod getMill = getMethodBy("getMill", millClass);
    //test Method Name
    assertEquals("getMill", getMill.getName());
    //test Parameters
    assertTrue(getMill.isEmptyCDParameters());
    //test ReturnType
    assertTrue(getMill.getMCReturnType().isPresentMCType());
    assertDeepEquals("AutomatonMill", getMill.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PROTECTED_STATIC.build().deepEquals(getMill.getModifier()));
  }

  @Test
  public void testInitMeMethod() {
    ASTCDMethod initMe = getMethodBy("initMe", millClass);
    //test Method Name
    assertEquals("initMe", initMe.getName());
    //test Parameters
    assertEquals(1, initMe.sizeCDParameters());
    assertDeepEquals("AutomatonMill", initMe.getCDParameter(0).getMCType());
    assertEquals("a", initMe.getCDParameter(0).getName());
    //test ReturnType
    assertTrue(initMe.getMCReturnType().isPresentMCVoidType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(initMe.getModifier()));
  }

  @Test
  public void testInitMethod() {
    ASTCDMethod init = getMethodBy("init", millClass);
    //test Method Name
    assertEquals("init", init.getName());
    //test Parameters
    assertTrue(init.isEmptyCDParameters());
    //test ReturnType
    assertTrue(init.getMCReturnType().isPresentMCVoidType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(init.getModifier()));
  }

  @Test
  public void testResetMethod() {
    ASTCDMethod reset = getMethodBy("reset", millClass);
    //test Method Name
    assertEquals("reset", reset.getName());
    //test Parameters
    assertTrue(reset.isEmptyCDParameters());
    //test ReturnType
    assertTrue(reset.getMCReturnType().isPresentMCVoidType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(reset.getModifier()));
  }

  @Test
  public void testAutomatonBuilderMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("automatonBuilder", millClass);
    //test Method Name
    assertEquals("automatonBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.ast.ASTAutomatonBuilder", fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testProtectedAutomatonMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("_automatonBuilder", millClass);
    //test Method Name
    assertEquals("_automatonBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.ast.ASTAutomatonBuilder", fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PROTECTED.build().deepEquals(fooBarBuilder.getModifier()));
  }


  @Test
  public void testStateMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("stateBuilder", millClass);
    //test Method Name
    assertEquals("stateBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.ast.ASTStateBuilder", fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testProtectedStateBuilderMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("_stateBuilder", millClass);
    //test Method Name
    assertEquals("_stateBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.ast.ASTStateBuilder", fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PROTECTED.build().deepEquals(fooBarBuilder.getModifier()));
  }


  @Test
  public void testTransitionMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("transitionBuilder", millClass);
    //test Method Name
    assertEquals("transitionBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.ast.ASTTransitionBuilder", fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testProtectedTransitionBuilderMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("_transitionBuilder", millClass);
    //test Method Name
    assertEquals("_transitionBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.ast.ASTTransitionBuilder", fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PROTECTED.build().deepEquals(fooBarBuilder.getModifier()));
  }


  @Test
  public void testDelegatorVisitorMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("automatonDelegatorVisitorBuilder", millClass);
    //test Method Name
    assertEquals("automatonDelegatorVisitorBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.ast.automaton._visitor.AutomatonDelegatorVisitorBuilder",
        fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testDelegatorVisitorBuilderMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("_automatonDelegatorVisitorBuilder", millClass);
    //test Method Name
    assertEquals("_automatonDelegatorVisitorBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.ast.automaton._visitor.AutomatonDelegatorVisitorBuilder",
        fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PROTECTED.build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, millClass, millClass);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  }
}
