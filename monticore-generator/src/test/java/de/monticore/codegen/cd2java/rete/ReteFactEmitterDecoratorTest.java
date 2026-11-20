/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.rete;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.mc2cd.MC2CD4CodeSymbolTableCompleter;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.rete.ReteConstants.FACT_EMITTER_TYPE;
import static de.monticore.codegen.cd2java.rete.ReteConstants.RETE_NETWORK_TYPE;
import static org.junit.jupiter.api.Assertions.*;

public class ReteFactEmitterDecoratorTest extends DecoratorTestCase {

  private static final String AST_PACKAGE = "de.monticore.codegen.rete.rete._ast.";

  private ASTCDCompilationUnit originalCompilationUnit;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDClass emitterClass;

  @BeforeEach
  public void setup() {
    decoratedCompilationUnit = parseAndComplete("Rete");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();

    ReteFactEmitterDecorator decorator = new ReteFactEmitterDecorator(glex,
        new ReteService(decoratedCompilationUnit), new VisitorService(decoratedCompilationUnit));
    this.emitterClass = decorator.decorate(decoratedCompilationUnit);
  }

  private ASTCDCompilationUnit parseAndComplete(String name) {
    ASTCDCompilationUnit compilationUnit = this.parse("de", "monticore", "codegen", "rete", name);
    compilationUnit.accept(new MC2CD4CodeSymbolTableCompleter().getTraverser());
    return compilationUnit;
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);

    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testClass() {
    assertEquals("ReteReteFactEmitter", emitterClass.getName());
    assertDeepEquals(PUBLIC, emitterClass.getModifier());
    assertEquals(FACT_EMITTER_TYPE, CD4CodeMill.prettyPrint(emitterClass.getCDExtendUsage().getSuperclass(0), false));
    assertEquals(1, emitterClass.getInterfaceList().size());
    assertEquals("de.monticore.codegen.rete.rete._visitor.ReteVisitor2",
        CD4CodeMill.prettyPrint(emitterClass.getCDInterfaceUsage().getInterface(0), false));

    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testConstructor() {
    assertEquals(1, emitterClass.getCDConstructorList().size());
    assertDeepEquals(PUBLIC, emitterClass.getCDConstructorList().get(0).getModifier());
    assertEquals(1, emitterClass.getCDConstructorList().get(0).sizeCDParameters());
    assertEquals(RETE_NETWORK_TYPE, CD4CodeMill.prettyPrint(
        emitterClass.getCDConstructorList().get(0).getCDParameter(0).getMCType(), false));

    assertTrue(Log.getFindings().isEmpty());
  }

  /**
   * one visit method for each type declaring features, none for types without
   * features (ASTTable): the nodes are reported by the full emitter
   */
  @Test
  public void testVisitMethods() {
    List<String> visitedTypes = emitterClass.getCDMethodList().stream()
        .peek(m -> assertEquals("visit", m.getName()))
        .map(m -> CD4CodeMill.prettyPrint(m.getCDParameter(0).getMCType(), false))
        .toList();
    assertEquals(List.of(AST_PACKAGE + "ASTNamed", AST_PACKAGE + "ASTAutomaton", AST_PACKAGE + "ASTState",
        AST_PACKAGE + "ASTFinalState"), visitedTypes);
    for (ASTCDMethod method : emitterClass.getCDMethodList()) {
      assertDeepEquals(PUBLIC, method.getModifier());
      assertEquals("node", method.getCDParameter(0).getName());
    }

    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGeneratedCode() {
    String code = generate();

    assertTrue(code.contains("public void visit (" + AST_PACKAGE + "ASTNamed node) { "
        + "fact(ReteReteFeatures.NAMED_NAME, node, node.getName()); }"), code);
    // list and optional attributes
    assertTrue(code.contains("public void visit (" + AST_PACKAGE + "ASTAutomaton node) { "
        + "facts(ReteReteFeatures.AUTOMATON_STATES, node, node.getStatesList()); "
        + "if (node.isPresentComment()) { fact(ReteReteFeatures.AUTOMATON_COMMENT, node, node.getComment()); } }"), code);
    // escaped names and primitive attributes
    assertTrue(code.contains("public void visit (" + AST_PACKAGE + "ASTState node) { "
        + "fact(ReteReteFeatures.STATE_FINAL, node, node.isFinal()); "
        + "fact(ReteReteFeatures.STATE_KIND, node, node.getKind()); "
        + "facts(ReteReteFeatures.STATE_LABELS, node, node.getLabelsList()); }"), code);
    // the inherited attributes of the super class are reported by its visit method
    assertTrue(code.contains("public void visit (" + AST_PACKAGE + "ASTFinalState node) { "
        + "fact(ReteReteFeatures.FINAL_STATE_ACCEPTING, node, node.isAccepting()); }"), code);
    // the nodes are reported by the full emitter
    assertFalse(code.contains("node(node)"), code);

    assertTrue(Log.getFindings().isEmpty());
  }

  private String generate() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    CD4C.init(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CD2JavaTemplates.CLASS, emitterClass, packageDir);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult<?> parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful(), sb.toString());
    // ignore comments and layout
    return sb.toString().replaceAll("(?s)/\\*.*?\\*/", "").replaceAll("\\s+", " ");
  }

  @Test
  public void testDecorateIntoRetePackage() {
    ASTCDCompilationUnit decoratedCD = createEmptyCompilationUnit(decoratedCompilationUnit);
    new ReteFactEmitterDecorator(glex, new ReteService(decoratedCompilationUnit),
        new VisitorService(decoratedCompilationUnit)).decorate(decoratedCompilationUnit, decoratedCD);

    List<ASTCDPackage> packages = decoratedCD.getCDDefinition().getCDPackagesList();
    assertEquals(1, packages.size());
    assertEquals("de.monticore.codegen.rete.rete._rete", packages.get(0).getName());
    assertEquals("ReteReteFactEmitter", ((ASTCDClass) packages.get(0).getCDElement(0)).getName());

    assertTrue(Log.getFindings().isEmpty());
  }
}
