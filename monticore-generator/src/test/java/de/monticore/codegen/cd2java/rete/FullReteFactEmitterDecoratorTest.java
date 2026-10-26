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
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.mc2cd.MC2CD4CodeSymbolTableCompleter;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java.rete.ReteConstants.FULL_FACT_EMITTER_TYPE;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the fact emitters of composed languages: ReteStates and ReteLabels
 * both extend ReteBase, ReteComposed extends ReteStates and ReteLabels
 * (diamond). The full emitter of ReteComposed registers the emitters of all
 * four languages, each reporting only the features of its own types.
 */
public class FullReteFactEmitterDecoratorTest extends DecoratorTestCase {

  private ASTCDCompilationUnit composed;

  private ASTCDClass fullEmitterClass;

  @BeforeEach
  public void setup() {
    // super languages first, as the generator processes them before the sub language
    parseAndComplete("ReteBase");
    parseAndComplete("ReteStates");
    parseAndComplete("ReteLabels");
    composed = parseAndComplete("ReteComposed");

    fullEmitterClass = new FullReteFactEmitterDecorator(glex, new ReteService(composed),
        new VisitorService(composed)).decorate(composed);
  }

  private ASTCDCompilationUnit parseAndComplete(String name) {
    ASTCDCompilationUnit compilationUnit = this.parse("de", "monticore", "codegen", "rete", name);
    compilationUnit.accept(new MC2CD4CodeSymbolTableCompleter().getTraverser());
    return compilationUnit;
  }

  @Test
  public void testClass() {
    assertEquals("ReteComposedFullReteFactEmitter", fullEmitterClass.getName());
    assertDeepEquals(PUBLIC, fullEmitterClass.getModifier());
    assertEquals(FULL_FACT_EMITTER_TYPE,
        CD4CodeMill.prettyPrint(fullEmitterClass.getCDExtendUsage().getSuperclass(0), false));
    assertEquals(1, fullEmitterClass.getCDConstructorList().size());
    assertDeepEquals(PUBLIC, fullEmitterClass.getCDConstructorList().get(0).getModifier());

    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMethods() {
    ASTCDMethod initialize = getMethodBy("initializeTraverser", fullEmitterClass);
    assertDeepEquals(PROTECTED, initialize.getModifier());
    assertTrue(initialize.getMCReturnType().isPresentMCVoidType());

    ASTCDMethod getTraverser = getMethodBy("getTraverser", fullEmitterClass);
    assertDeepEquals(PUBLIC, getTraverser.getModifier());
    assertEquals("de.monticore.codegen.rete.retecomposed._visitor.ReteComposedTraverser",
        CD4CodeMill.prettyPrint(getTraverser.getMCReturnType().getMCType(), false));

    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGeneratedCode() {
    String code = generate(fullEmitterClass);

    assertTrue(code.contains("super(network, de.monticore.codegen.rete.retecomposed.ReteComposedMill"
        + ".inheritanceTraverser());"), code);
    assertTrue(code.contains("return (de.monticore.codegen.rete.retecomposed._visitor.ReteComposedTraverser) "
        + "traverser;"), code);
    // the emitters of the language and of all its transitive super languages
    assertRegistered(code, "retecomposed", "ReteComposed");
    assertRegistered(code, "retestates", "ReteStates");
    assertRegistered(code, "retelabels", "ReteLabels");
    assertRegistered(code, "retebase", "ReteBase");

    assertTrue(Log.getFindings().isEmpty());
  }

  private void assertRegistered(String code, String pkg, String language) {
    String emitter = "de.monticore.codegen.rete." + pkg + "._rete." + language + "ReteFactEmitter";
    String variable = Character.toLowerCase(language.charAt(0)) + language.substring(1) + "Emitter";
    assertTrue(code.contains(emitter + " " + variable + " = new " + emitter + "(network); "
        + "getTraverser().add4" + language + "(" + variable + "); "
        + "addEmitter(" + variable + ");"), code);
  }

  /**
   * the emitter of the sub language only reports the features declared by its
   * own types: the overriding ASTState reports its new attribute, the
   * inherited attributes are reported by the emitters of the super languages
   */
  @Test
  public void testComposedEmitter() {
    ASTCDClass emitterClass = new ReteFactEmitterDecorator(glex, new ReteService(composed),
        new VisitorService(composed)).decorate(composed);
    String code = generate(emitterClass);

    assertTrue(code.contains("public void visit (de.monticore.codegen.rete.retecomposed._ast.ASTState node) { "
        + "fact(ReteComposedReteFeatures.STATE_PRIORITY, node, node.getPriority()); }"), code);
    assertTrue(code.contains("public void visit (de.monticore.codegen.rete.retecomposed._ast.ASTLabeledState node) { "
        + "facts(ReteComposedReteFeatures.LABELED_STATE_LABELS, node, node.getLabelsList()); }"), code);
    assertFalse(code.contains("STATE_NAME"), code);
    assertFalse(code.contains("ELEMENT_ID"), code);

    assertTrue(Log.getFindings().isEmpty());
  }

  private String generate(ASTCDClass clazz) {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    CD4C.init(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CD2JavaTemplates.CLASS, clazz, packageDir);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult<?> parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful(), sb.toString());
    // ignore comments and layout
    return sb.toString().replaceAll("(?s)/\\*.*?\\*/", "").replaceAll("\\s+", " ");
  }
}
