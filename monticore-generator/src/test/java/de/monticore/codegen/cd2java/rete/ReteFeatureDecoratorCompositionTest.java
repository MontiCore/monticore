/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.rete;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.mc2cd.MC2CD4CodeSymbolTableCompleter;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.types.MCTypeFacade;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getAttributeBy;
import static de.monticore.codegen.cd2java.rete.ReteConstants.FEATURE_TYPE;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the features classes of composed languages: ReteStates and ReteLabels
 * both extend ReteBase, ReteComposed extends ReteStates and ReteLabels
 * (diamond). Inherited attributes are reported under the features of the super
 * language that declares them, so each attribute has exactly one feature in
 * the whole language family.
 */
public class ReteFeatureDecoratorCompositionTest extends DecoratorTestCase {

  private static final String BASE_FEATURES = "de.monticore.codegen.rete.retebase._rete.ReteBaseReteFeatures";

  private static final String STATES_FEATURES = "de.monticore.codegen.rete.retestates._rete.ReteStatesReteFeatures";

  private static final String LABELS_FEATURES = "de.monticore.codegen.rete.retelabels._rete.ReteLabelsReteFeatures";

  private ASTCDClass baseFeatures;

  private ASTCDClass statesFeatures;

  private ASTCDClass composedFeatures;

  private ASTCDCompilationUnit base;

  private ASTCDCompilationUnit states;

  private ASTCDCompilationUnit composed;

  @BeforeEach
  public void setup() {
    // super languages first, as the generator processes them before the sub language
    base = parseAndComplete("ReteBase");
    states = parseAndComplete("ReteStates");
    parseAndComplete("ReteLabels");
    composed = parseAndComplete("ReteComposed");

    baseFeatures = new ReteFeatureDecorator(glex, new ReteService(base)).decorate(base);
    statesFeatures = new ReteFeatureDecorator(glex, new ReteService(states)).decorate(states);
    composedFeatures = new ReteFeatureDecorator(glex, new ReteService(composed)).decorate(composed);
  }

  private Map<String, Map<String, String>> declaringTypes(ASTCDCompilationUnit compilationUnit) {
    ReteFeatureDecorator decorator = new ReteFeatureDecorator(glex, new ReteService(compilationUnit));
    ASTCDDefinition cdDefinition = compilationUnit.getCDDefinition();
    return decorator.createDeclaringTypes(cdDefinition, decorator.createFeatureConstants(cdDefinition));
  }

  private Map<String, Map<String, String>> featureConstants(ASTCDCompilationUnit compilationUnit) {
    return new ReteFeatureDecorator(glex, new ReteService(compilationUnit))
        .createFeatureConstants(compilationUnit.getCDDefinition());
  }

  /**
   * resolveConstant resolves the features of the language itself directly and attributes whose
   * feature is declared by a super language via the super languages, passing the declaring type:
   * ASTState of ReteStates inherits id from the interface ASTElement of ReteBase
   */
  @Test
  public void testResolveConstantOfSuperLanguages() {
    String baseElement = "de.monticore.codegen.rete.retebase._ast.ASTElement";
    String statesState = "de.monticore.codegen.rete.retestates._ast.ASTState";

    assertEquals(Map.of(baseElement, Map.of("id", baseElement)), declaringTypes(base));
    assertEquals(Map.of(baseElement, Map.of("id", BASE_FEATURES + ".ELEMENT_ID")), featureConstants(base));

    assertEquals(Map.of(statesState, Map.of("id", baseElement, "initial", statesState, "name", statesState)),
        declaringTypes(states));
    assertEquals(Map.of(statesState, Map.of(
            "initial", STATES_FEATURES + ".STATE_INITIAL",
            "name", STATES_FEATURES + ".STATE_NAME")),
        featureConstants(states));

    assertTrue(Log.getFindings().isEmpty());
  }

  /**
   * the overriding ASTState of ReteComposed inherits name and initial from ASTState of ReteStates
   * (which repeats id of ReteBase's ASTElement); the feature of the type declaring the attribute
   * first applies, even across the diamond
   */
  @Test
  public void testResolveConstantOfComposedLanguage() {
    String composedFeaturesClass = "de.monticore.codegen.rete.retecomposed._rete.ReteComposedReteFeatures";
    String composedState = "de.monticore.codegen.rete.retecomposed._ast.ASTState";
    String composedLabeledState = "de.monticore.codegen.rete.retecomposed._ast.ASTLabeledState";
    String statesState = "de.monticore.codegen.rete.retestates._ast.ASTState";
    String baseElement = "de.monticore.codegen.rete.retebase._ast.ASTElement";

    assertEquals(Map.of(
            composedState, Map.of(
                "id", baseElement,
                "initial", statesState,
                "name", statesState,
                "priority", composedState),
            composedLabeledState, Map.of(
                "id", baseElement,
                "labels", composedLabeledState)),
        declaringTypes(composed));
    assertEquals(Map.of(
            composedState, Map.of("priority", composedFeaturesClass + ".STATE_PRIORITY"),
            composedLabeledState, Map.of("labels", composedFeaturesClass + ".LABELED_STATE_LABELS")),
        featureConstants(composed));

    assertTrue(Log.getFindings().isEmpty());
  }

  private ASTCDCompilationUnit parseAndComplete(String name) {
    ASTCDCompilationUnit compilationUnit = this.parse("de", "monticore", "codegen", "rete", name);
    compilationUnit.accept(new MC2CD4CodeSymbolTableCompleter().getTraverser());
    return compilationUnit;
  }

  private static List<String> constantNames(ASTCDClass featureClass) {
    return featureClass.getCDAttributeList().stream().map(ASTCDAttribute::getName).toList();
  }

  @Test
  public void testBaseLanguage() {
    assertEquals("ReteBaseReteFeatures", baseFeatures.getName());
    assertEquals(List.of("ELEMENT_ID", "ALL", "ALL_TRANSITIVE"), constantNames(baseFeatures));

    assertTrue(Log.getFindings().isEmpty());
  }

  /**
   * ASTState implements ASTElement of the super language ReteBase, so its id is
   * reported under ReteBase's ELEMENT_ID
   */
  @Test
  public void testInterfaceOfSuperLanguage() {
    assertEquals(List.of("STATE_NAME", "STATE_INITIAL", "ALL", "ALL_TRANSITIVE"), constantNames(statesFeatures));

    assertTrue(Log.getFindings().isEmpty());
  }

  /**
   * the overriding ASTState only gets a feature for its new attribute priority
   * (name is covered by STATE_NAME of ReteStates), ASTLabeledState only for
   * labels (id is covered by ELEMENT_ID of the transitive super language ReteBase)
   */
  @Test
  public void testComposedLanguage() {
    assertEquals("ReteComposedReteFeatures", composedFeatures.getName());
    assertEquals(List.of("STATE_PRIORITY", "LABELED_STATE_LABELS", "ALL", "ALL_TRANSITIVE"),
        constantNames(composedFeatures));

    MCTypeFacade mcTypeFacade = MCTypeFacade.getInstance();
    assertDeepEquals(mcTypeFacade.createBasicGenericTypeOf(FEATURE_TYPE,
            "de.monticore.codegen.rete.retecomposed._ast.ASTState", "Integer"),
        getAttributeBy("STATE_PRIORITY", composedFeatures).getMCType());
    assertDeepEquals(mcTypeFacade.createBasicGenericTypeOf(FEATURE_TYPE,
            "de.monticore.codegen.rete.retecomposed._ast.ASTLabeledState",
            "de.monticore.codegen.rete.retelabels._ast.ASTLabel"),
        getAttributeBy("LABELED_STATE_LABELS", composedFeatures).getMCType());

    assertTrue(Log.getFindings().isEmpty());
  }

  /**
   * ALL_TRANSITIVE of the composed language combines its own features with
   * those of both super languages and of ReteBase, which is reachable twice
   * (diamond) but must be included only once
   */
  @Test
  public void testAllTransitive() {
    String composedCode = generate(composedFeatures);
    // the declaration, not the mention in the class javadoc
    String allTransitive = composedCode.substring(composedCode.indexOf("> ALL_TRANSITIVE"));
    allTransitive = allTransitive.substring(0, allTransitive.indexOf(';'));

    assertTrue(allTransitive.contains("java.util.stream.Stream.of(ALL, "), allTransitive);
    assertTrue(allTransitive.contains(".flatMap(java.util.List::stream).toList()"), allTransitive);
    assertEquals(1, count(allTransitive, STATES_FEATURES + ".ALL"), allTransitive);
    assertEquals(1, count(allTransitive, LABELS_FEATURES + ".ALL"), allTransitive);
    assertEquals(1, count(allTransitive, BASE_FEATURES + ".ALL"), allTransitive);

    // a language without super languages only lists its own features
    String baseCode = generate(baseFeatures);
    assertTrue(baseCode.contains("java.util.stream.Stream.of(ALL).flatMap(java.util.List::stream).toList()"), baseCode);

    assertTrue(Log.getFindings().isEmpty());
  }

  private String generate(ASTCDClass featureClass) {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    CD4C.init(generatorSetup);
    String code = generatorEngine.generate(CD2JavaTemplates.CLASS, featureClass, packageDir).toString();
    // resolveConstant uses switch expressions
    ParseResult<?> parseResult = new JavaParser(new ParserConfiguration()
        .setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17)).parse(code);
    assertTrue(parseResult.isSuccessful(), code);
    return code;
  }

  private static int count(String text, String part) {
    int count = 0;
    for (int i = text.indexOf(part); i >= 0; i = text.indexOf(part, i + 1)) {
      count++;
    }
    return count;
  }
}
