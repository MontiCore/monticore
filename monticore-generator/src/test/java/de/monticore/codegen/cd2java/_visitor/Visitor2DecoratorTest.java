/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.types.MCTypeFacade;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodsBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Visitor2DecoratorTest extends DecoratorTestCase {

  public static final String ASTAUTOMATON = "de.monticore.codegen.ast.automaton._ast.ASTAutomaton";

  public static final String ASTAUTOMATONNODE = "de.monticore.codegen.ast.automaton._ast.ASTAutomatonNode";

  public static final String STATESYMBOL = "de.monticore.codegen.ast.automaton._symboltable.StateSymbol";

  public static final String ISYMBOL = "de.monticore.symboltable.ISymbol";

  public static final String ISCOPE = "de.monticore.symboltable.IScope";

  public static final String AUTOMATONSCOPE = "de.monticore.codegen.ast.automaton._symboltable.IAutomatonScope";

  public static final String AUTOMATONARTIFACTSCOPE = "de.monticore.codegen.ast.automaton._symboltable.IAutomatonArtifactScope";

  public static final String ASTNODE = "de.monticore.ast.ASTNode";


  private MCTypeFacade mcTypeFacade;

  private ASTCDInterface visitor2;

  private ASTCDCompilationUnit originalCompilationUnit;

  private ASTCDCompilationUnit decoratedCompilationUnit;


  @Before
  public void setUp() {
    this.mcTypeFacade = MCTypeFacade.getInstance();

    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();

    this.glex.setGlobalValue("service", new VisitorService(decoratedCompilationUnit));
    VisitorService visitorService = new VisitorService(decoratedCompilationUnit);
    SymbolTableService symbolTableService = new SymbolTableService(decoratedCompilationUnit);

    Visitor2Decorator visitor2Decorator = new Visitor2Decorator(this.glex, visitorService, symbolTableService);
    this.visitor2 = visitor2Decorator.decorate(decoratedCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testVisitorName() {
    assertEquals("AutomatonVisitor2", visitor2.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAttributeCount() {
    assertEquals(0, visitor2.getCDAttributeList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMethodCount() {
    assertEquals(20, visitor2.getCDMethodList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInterfaceCount() {
    assertEquals(1, visitor2.getInterfaceList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testVisitASTAutomaton() {
    List<ASTCDMethod> list = getMethodsBy("visit", 1, visitor2);
    List<ASTCDMethod> methods = list.stream()
        .filter(m -> CD4CodeMill.prettyPrint(m.getCDParameter(0).getMCType(), false).equals(ASTAUTOMATON))
        .collect(Collectors.toList());
    assertEquals(1, methods.size());
    ASTCDMethod method = methods.get(0);
    assertEquals("node", method.getCDParameter(0).getName());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testEndVisitASTAutomaton() {
    List<ASTCDMethod> list = getMethodsBy("endVisit", 1, visitor2);
    List<ASTCDMethod> methods = list.stream()
        .filter(m -> CD4CodeMill.prettyPrint(m.getCDParameter(0).getMCType(), false).equals(ASTAUTOMATON))
        .collect(Collectors.toList());
    assertEquals(1, methods.size());
    ASTCDMethod method = methods.get(0);
    assertEquals("node", method.getCDParameter(0).getName());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testVisitASTAutomatonNode() {
    List<ASTCDMethod> list = getMethodsBy("visit", 1, visitor2);
    List<ASTCDMethod> methods = list.stream()
        .filter(m -> CD4CodeMill.prettyPrint(m.getCDParameter(0).getMCType(), false).equals(ASTAUTOMATONNODE))
        .collect(Collectors.toList());
    assertEquals(1, methods.size());
    ASTCDMethod method = methods.get(0);
    assertEquals("node", method.getCDParameter(0).getName());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testEndVisitASTAutomatonNode() {
    List<ASTCDMethod> list = getMethodsBy("endVisit", 1, visitor2);
    List<ASTCDMethod> methods = list.stream()
        .filter(m -> CD4CodeMill.prettyPrint(m.getCDParameter(0).getMCType(), false).equals(ASTAUTOMATONNODE))
        .collect(Collectors.toList());
    assertEquals(1, methods.size());
    ASTCDMethod method = methods.get(0);
    assertEquals("node", method.getCDParameter(0).getName());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testVisitStateSymbol() {
    List<ASTCDMethod> list = getMethodsBy("visit", 1, visitor2);
    List<ASTCDMethod> methods = list.stream()
        .filter(m -> CD4CodeMill.prettyPrint(m.getCDParameter(0).getMCType(), false).equals(STATESYMBOL))
        .collect(Collectors.toList());
    assertEquals(1, methods.size());
    ASTCDMethod method = methods.get(0);
    assertEquals("node", method.getCDParameter(0).getName());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testEndVisitStateSymbol() {
    List<ASTCDMethod> list = getMethodsBy("endVisit", 1, visitor2);
    List<ASTCDMethod> methods = list.stream()
        .filter(m -> CD4CodeMill.prettyPrint(m.getCDParameter(0).getMCType(), false).equals(STATESYMBOL))
        .collect(Collectors.toList());
    assertEquals(1, methods.size());
    ASTCDMethod method = methods.get(0);
    assertEquals("node", method.getCDParameter(0).getName());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testVisitIAutomatonScope() {
    List<ASTCDMethod> list = getMethodsBy("visit", 1, visitor2);
    List<ASTCDMethod> methods = list.stream()
        .filter(m -> CD4CodeMill.prettyPrint(m.getCDParameter(0).getMCType(), false).equals(AUTOMATONSCOPE))
        .collect(Collectors.toList());
    assertEquals(1, methods.size());
    ASTCDMethod method = methods.get(0);
    assertEquals("node", method.getCDParameter(0).getName());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testEndVisitIAutomatonScope() {
    List<ASTCDMethod> list = getMethodsBy("endVisit", 1, visitor2);
    List<ASTCDMethod> methods = list.stream()
        .filter(m -> CD4CodeMill.prettyPrint(m.getCDParameter(0).getMCType(), false).equals(AUTOMATONSCOPE))
        .collect(Collectors.toList());
    assertEquals(1, methods.size());
    ASTCDMethod method = methods.get(0);
    assertEquals("node", method.getCDParameter(0).getName());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testVisitIAutomatonArtifactScope() {
    List<ASTCDMethod> list = getMethodsBy("visit", 1, visitor2);
    List<ASTCDMethod> methods = list.stream()
        .filter(m -> CD4CodeMill.prettyPrint(m.getCDParameter(0).getMCType(), false).equals(AUTOMATONARTIFACTSCOPE))
        .collect(Collectors.toList());
    assertEquals(1, methods.size());
    ASTCDMethod method = methods.get(0);
    assertEquals("node", method.getCDParameter(0).getName());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testEndVisitIAutomatonArtifactScope() {
    List<ASTCDMethod> list = getMethodsBy("endVisit", 1, visitor2);
    List<ASTCDMethod> methods = list.stream()
        .filter(m -> CD4CodeMill.prettyPrint(m.getCDParameter(0).getMCType(), false).equals(AUTOMATONARTIFACTSCOPE))
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
    StringBuilder sb = generatorEngine.generate(CD2JavaTemplates.INTERFACE, visitor2, packageDir);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}

