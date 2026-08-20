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
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodsBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Visitor2DecoratorTest extends DecoratorTestCase {

  public static final String VISIT_METHOD = "visit";
  public static final String END_VISIT_METHOD = "endVisit";

  public static final String AUTOMATON_NODE =
      "de.monticore.codegen.ast.automaton._ast.ASTAutomatonNode";
  public static final String ABSTRACT_CLASS =
      "de.monticore.codegen.ast.automaton._ast.ASTAbstractClass";
  public static final String AUTOMATON =
      "de.monticore.codegen.ast.automaton._ast.ASTAutomaton";
  public static final String STATE =
      "de.monticore.codegen.ast.automaton._ast.ASTState";
  public static final String TRANSITION =
      "de.monticore.codegen.ast.automaton._ast.ASTTransition";
  public static final String STATE_SYMBOL =
      "de.monticore.codegen.ast.automaton._symboltable.StateSymbol";
  public static final String AUTOMATON_SYMBOL =
      "de.monticore.codegen.ast.automaton._symboltable.AutomatonSymbol";
  public static final String COMMON_AUTOMATON_SYMBOL =
      "de.monticore.codegen.ast.automaton._symboltable.ICommonAutomatonSymbol";
  public static final String AUTOMATON_SCOPE =
      "de.monticore.codegen.ast.automaton._symboltable.IAutomatonScope";
  public static final String AUT_ARTIFACT_SCOPE =
      "de.monticore.codegen.ast.automaton._symboltable.IAutomatonArtifactScope";
  public static final String AUT_GLOBAL_SCOPE =
      "de.monticore.codegen.ast.automaton._symboltable.IAutomatonGlobalScope";

  public static final String ASTNODE = "de.monticore.ast.ASTNode";
  public static final String ISYMBOL = "de.monticore.symboltable.ISymbol";
  public static final String ISCOPE = "de.monticore.symboltable.IScope";

  private static ASTCDInterface decoratedInterface;

  private ASTCDCompilationUnit originalCompilationUnit;

  private ASTCDCompilationUnit decoratedCompilationUnit;


  @BeforeEach
  public void setUp() {
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();

    this.glex.setGlobalValue("service", new VisitorService(decoratedCompilationUnit));
    VisitorService visitorService = new VisitorService(decoratedCompilationUnit);
    SymbolTableService symbolTableService = new SymbolTableService(decoratedCompilationUnit);

    Visitor2Decorator decoratedCDDecorator = new Visitor2Decorator(this.glex, visitorService, symbolTableService);
    decoratedInterface = decoratedCDDecorator.decorate(decoratedCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testVisitorName() {
    assertEquals("AutomatonVisitor2", decoratedInterface.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAttributeCount() {
    assertEquals(0, decoratedInterface.getCDAttributeList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMethodCount() {
    assertEquals(22, decoratedInterface.getCDMethodList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInterfaceCount() {
    assertEquals(1, decoratedInterface.getInterfaceList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  static Stream<Arguments> testVisitorMethodsArgs() {
    return Stream.of(
        Arguments.of(VISIT_METHOD, AUTOMATON_NODE, "node", decoratedInterface),
        Arguments.of(END_VISIT_METHOD, AUTOMATON_NODE, "node", decoratedInterface),

        Arguments.of(VISIT_METHOD, ABSTRACT_CLASS, "node", decoratedInterface),
        Arguments.of(END_VISIT_METHOD, ABSTRACT_CLASS, "node", decoratedInterface),

        Arguments.of(VISIT_METHOD, AUTOMATON, "node", decoratedInterface),
        Arguments.of(END_VISIT_METHOD, AUTOMATON, "node", decoratedInterface),

        Arguments.of(VISIT_METHOD, STATE, "node", decoratedInterface),
        Arguments.of(END_VISIT_METHOD, STATE, "node", decoratedInterface),

        Arguments.of(VISIT_METHOD, TRANSITION, "node", decoratedInterface),
        Arguments.of(END_VISIT_METHOD, TRANSITION, "node", decoratedInterface),

        Arguments.of(VISIT_METHOD, STATE_SYMBOL, "node", decoratedInterface),
        Arguments.of(END_VISIT_METHOD, STATE_SYMBOL, "node", decoratedInterface),

        Arguments.of(VISIT_METHOD, AUTOMATON_SYMBOL, "node", decoratedInterface),
        Arguments.of(END_VISIT_METHOD, AUTOMATON_SYMBOL, "node", decoratedInterface),

        Arguments.of(VISIT_METHOD, COMMON_AUTOMATON_SYMBOL, "node", decoratedInterface),
        Arguments.of(END_VISIT_METHOD, COMMON_AUTOMATON_SYMBOL, "node", decoratedInterface),

        Arguments.of(VISIT_METHOD, AUTOMATON_SCOPE, "node", decoratedInterface),
        Arguments.of(END_VISIT_METHOD, AUTOMATON_SCOPE, "node", decoratedInterface),

        Arguments.of(VISIT_METHOD, AUT_ARTIFACT_SCOPE, "node", decoratedInterface),
        Arguments.of(END_VISIT_METHOD, AUT_ARTIFACT_SCOPE, "node", decoratedInterface),

        Arguments.of(VISIT_METHOD, AUT_GLOBAL_SCOPE, "node", decoratedInterface),
        Arguments.of(END_VISIT_METHOD, AUT_GLOBAL_SCOPE, "node", decoratedInterface)
    );
  }

  @ParameterizedTest
  @MethodSource("testVisitorMethodsArgs")
  public void testVisitorMethods(String methodName, String parameterType, String parameterName, ASTCDInterface visitor) {
    List<ASTCDMethod> list = getMethodsBy(methodName, 1, visitor);
    List<ASTCDMethod> methods = list.stream()
        .filter(m -> CD4CodeMill.prettyPrint(m.getCDParameter(0).getMCType(), false).equals(parameterType))
        .toList();
    assertEquals(1, methods.size());
    ASTCDMethod method = methods.getFirst();
    assertEquals(parameterName, method.getCDParameter(0).getName());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertTrue(Log.getFindings().isEmpty());
  }

  static Stream<Arguments> testUnwantedVisitorMethodsArgs() {
    return Stream.of(
        Arguments.of(VISIT_METHOD, ISYMBOL, decoratedInterface),
        Arguments.of(END_VISIT_METHOD, ISYMBOL, decoratedInterface),

        Arguments.of(VISIT_METHOD, ISCOPE, decoratedInterface),
        Arguments.of(END_VISIT_METHOD, ISCOPE, decoratedInterface),

        Arguments.of(VISIT_METHOD, ASTNODE, decoratedInterface),
        Arguments.of(END_VISIT_METHOD, ASTNODE, decoratedInterface)
    );
  }

  @ParameterizedTest
  @MethodSource("testUnwantedVisitorMethodsArgs")
  public void testUnwantedVisitorMethods(String methodName, String parameterType, ASTCDInterface visitor) {
    List<ASTCDMethod> list = getMethodsBy(methodName, 1, visitor);
    List<ASTCDMethod> methods = list.stream()
        .filter(m -> CD4CodeMill.prettyPrint(m.getCDParameter(0).getMCType(), false).equals(parameterType))
        .toList();
    assertEquals(0, methods.size());

    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    CD4C.init(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CD2JavaTemplates.INTERFACE, decoratedInterface, packageDir);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult<?> parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
