/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodsBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HandlerDecoratorTest extends DecoratorTestCase {

  public static final String ASTAUTOMATON = "de.monticore.codegen.ast.automaton._ast.ASTAutomaton";

  public static final String ASTAUTOMATONNODE = "de.monticore.codegen.ast.automaton._ast.ASTAutomatonNode";

  public static final String AUTOMATONSYMBOL = "de.monticore.codegen.ast.automaton._symboltable.AutomatonSymbol";

  public static final String AUTOMATONSCOPE = "de.monticore.codegen.ast.automaton._symboltable.IAutomatonScope";

  public static final String AUTOMATONARTIFACTSCOPE = "de.monticore.codegen.ast.automaton._symboltable.IAutomatonArtifactScope";


  private MCTypeFacade mcTypeFacade;

  private ASTCDInterface handler;

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit originalCompilationUnit;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  @Before
  public void setUp() {
    LogStub.init();
    Log.enableFailQuick(false);
    this.glex = new GlobalExtensionManagement();
    this.mcTypeFacade = MCTypeFacade.getInstance();

    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();

    this.glex.setGlobalValue("service", new VisitorService(decoratedCompilationUnit));
    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
    VisitorService visitorService = new VisitorService(decoratedCompilationUnit);
    SymbolTableService symbolTableService = new SymbolTableService(decoratedCompilationUnit);

    HandlerDecorator decorator = new HandlerDecorator(this.glex, visitorService, symbolTableService);
    this.handler = decorator.decorate(decoratedCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testVisitorName() {
    assertEquals("AutomatonHandler", handler.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAttributeCount() {
    assertEquals(0, handler.getCDAttributeList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMethodCount() {
    assertEquals(20, handler.getCDMethodList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test 
  public void testInterfaceCount() {
    assertEquals(1, handler.getInterfaceList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetTraverser() {
    ASTCDMethod astcdMethod = getMethodBy("getTraverser", handler);
    assertDeepEquals(PUBLIC.build(), astcdMethod.getModifier());
    assertTrue(astcdMethod.getMCReturnType().isPresentMCType());
    assertEquals(0, astcdMethod.sizeCDParameters());
    assertDeepEquals("de.monticore.codegen.ast.automaton._visitor.AutomatonTraverser", astcdMethod.getMCReturnType().getMCType());
    
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSetTraverser() {
    ASTCDMethod astcdMethod = getMethodBy("setTraverser", handler);
    assertDeepEquals(PUBLIC.build(), astcdMethod.getModifier());
    assertTrue(astcdMethod.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, astcdMethod.sizeCDParameters());
    ASTCDParameter astcdParameter = astcdMethod.getCDParameter(0);
    assertEquals("traverser", astcdParameter.getName());
    assertDeepEquals("de.monticore.codegen.ast.automaton._visitor.AutomatonTraverser", astcdParameter.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testHandleAstautomaton() {
    List<ASTCDMethod> list = getMethodsBy("handle", 1, handler);
    List<ASTCDMethod> methods = list.stream()
        .filter(m -> m.getCDParameter(0).getMCType().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).equals(ASTAUTOMATON))
        .collect(Collectors.toList());
    assertEquals(1, methods.size());
    ASTCDMethod method = methods.get(0);
    assertEquals("node", method.getCDParameter(0).getName());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testTraverseAstautomaton() {
    List<ASTCDMethod> list = getMethodsBy("traverse", 1, handler);
    List<ASTCDMethod> methods = list.stream()
        .filter(m -> m.getCDParameter(0).getMCType().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).equals(ASTAUTOMATON))
        .collect(Collectors.toList());
    assertEquals(1, methods.size());
    ASTCDMethod method = methods.get(0);
    assertEquals("node", method.getCDParameter(0).getName());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testHandleAstautomatonNode() {
    List<ASTCDMethod> list = getMethodsBy("handle", 1, handler);
    List<ASTCDMethod> methods = list.stream()
        .filter(m -> m.getCDParameter(0).getMCType().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).equals(ASTAUTOMATONNODE))
        .collect(Collectors.toList());
    assertEquals(1, methods.size());
    ASTCDMethod method = methods.get(0);
    assertEquals("node", method.getCDParameter(0).getName());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testHandleAstautomatonSymbol() {
    List<ASTCDMethod> list = getMethodsBy("handle", 1, handler);
    List<ASTCDMethod> methods = list.stream()
        .filter(m -> m.getCDParameter(0).getMCType().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).equals(AUTOMATONSYMBOL))
        .collect(Collectors.toList());
    assertEquals(1, methods.size());
    ASTCDMethod method = methods.get(0);
    assertEquals("node", method.getCDParameter(0).getName());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testTraverseAstautomatonSymbol() {
    List<ASTCDMethod> list = getMethodsBy("traverse", 1, handler);
    List<ASTCDMethod> methods = list.stream()
        .filter(m -> m.getCDParameter(0).getMCType().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).equals(AUTOMATONSYMBOL))
        .collect(Collectors.toList());
    assertEquals(1, methods.size());
    ASTCDMethod method = methods.get(0);
    assertEquals("node", method.getCDParameter(0).getName());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testHandleAstautomatonScope() {
    List<ASTCDMethod> list = getMethodsBy("handle", 1, handler);
    List<ASTCDMethod> methods = list.stream()
        .filter(m -> m.getCDParameter(0).getMCType().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).equals(AUTOMATONSCOPE))
        .collect(Collectors.toList());
    assertEquals(1, methods.size());
    ASTCDMethod method = methods.get(0);
    assertEquals("node", method.getCDParameter(0).getName());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testTraverseAstautomatonScope() {
    List<ASTCDMethod> list = getMethodsBy("traverse", 1, handler);
    List<ASTCDMethod> methods = list.stream()
        .filter(m -> m.getCDParameter(0).getMCType().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).equals(AUTOMATONSCOPE))
        .collect(Collectors.toList());
    assertEquals(1, methods.size());
    ASTCDMethod method = methods.get(0);
    assertEquals("node", method.getCDParameter(0).getName());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testHandleAstautomatonArifactAScope() {
    List<ASTCDMethod> list = getMethodsBy("handle", 1, handler);
    List<ASTCDMethod> methods = list.stream()
        .filter(m -> m.getCDParameter(0).getMCType().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).equals(AUTOMATONARTIFACTSCOPE))
        .collect(Collectors.toList());
    assertEquals(1, methods.size());
    ASTCDMethod method = methods.get(0);
    assertEquals("node", method.getCDParameter(0).getName());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testTraverseAstautomatonArifactAScope() {
    List<ASTCDMethod> list = getMethodsBy("traverse", 1, handler);
    List<ASTCDMethod> methods = list.stream()
        .filter(m -> m.getCDParameter(0).getMCType().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).equals(AUTOMATONARTIFACTSCOPE))
        .collect(Collectors.toList());
    assertEquals(1, methods.size());
    ASTCDMethod method = methods.get(0);
    assertEquals("node", method.getCDParameter(0).getName());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    CD4C.init(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CD2JavaTemplates.INTERFACE, handler, packageDir);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  
    assertTrue(Log.getFindings().isEmpty());
  }

}



