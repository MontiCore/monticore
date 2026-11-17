/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.rete;

import de.monticore.MontiCoreScript;
import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisGlobalScope;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._symboltable.IGrammar_WithConceptsGlobalScope;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Derives the class diagram from a grammar like the generator does. The
 * ListSuffixDecorator renames list attributes (param to params) after the
 * symbol table has been created, so a list attribute repeated in an
 * implementing production must still be recognized as declared by the
 * interface.
 */
public class ReteListSuffixTest extends DecoratorTestCase {

  private ASTCDCompilationUnit cd;

  @BeforeEach
  public void setup() {
    Grammar_WithConceptsMill.reset();
    Grammar_WithConceptsMill.init();

    MCPath modelPath = new MCPath(Paths.get("src/test/resources"));
    MontiCoreScript mc = new MontiCoreScript();
    Path grammarPath = Paths.get("src/test/resources/de/monticore/codegen/rete/ReteListSuffix.mc4").toAbsolutePath();
    ASTMCGrammar grammar = mc.parseGrammar(grammarPath).orElseThrow();
    IGrammar_WithConceptsGlobalScope grammarScope = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(grammarScope, grammar);
    ICD4AnalysisGlobalScope cdScope = mc.createCD4AGlobalScope(modelPath);
    cd = mc.addListSuffixToAttributeName(mc.deriveASTCD(grammar, glex, cdScope));
  }

  @Test
  public void testFeatures() {
    ASTCDClass features = new ReteFeatureDecorator(glex, new ReteService(cd)).decorate(cd);

    assertEquals(List.of("SIGNATURE_NAME", "SIGNATURE_PARAMS", "PARAM_NAME", "ALL", "ALL_TRANSITIVE"),
        features.getCDAttributeList().stream().map(ASTCDAttribute::getName).toList());
    assertTrue(Log.getFindings().isEmpty());
  }

  /**
   * resolveConstant uses the attribute names of the grammar (param), not the names with list
   * suffix (params); Method repeats name and param of the interface Signature, whose features apply
   */
  @Test
  public void testResolveConstant() {
    String astPackage = "de.monticore.codegen.rete.retelistsuffix._ast.";
    String features = "java.util.Optional.of(\"de.monticore.codegen.rete.retelistsuffix._rete.ReteListSuffixReteFeatures.";

    Map<String, String> signatureCases = Map.of(
        "name", features + "SIGNATURE_NAME\")",
        "param", features + "SIGNATURE_PARAMS\")");

    assertEquals(Map.of(
            astPackage + "ASTSignature", signatureCases,
            astPackage + "ASTMethod", signatureCases,
            astPackage + "ASTParam", Map.of("name", features + "PARAM_NAME\")")),
        new ReteFeatureDecorator(glex, new ReteService(cd)).createResolveConstantCases(cd.getCDDefinition()));
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testFactEmitter() {
    ASTCDClass emitter = new ReteFactEmitterDecorator(glex, new ReteService(cd), new VisitorService(cd)).decorate(cd);
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    CD4C.init(generatorSetup);
    String code = new GeneratorEngine(generatorSetup).generate(CD2JavaTemplates.CLASS, emitter, packageDir).toString()
        .replaceAll("(?s)/\\*.*?\\*/", "").replaceAll("\\s+", " ");

    // the interface reports the list, the implementing class has no features of its own
    assertTrue(code.contains("ReteListSuffixReteFeatures.SIGNATURE_PARAMS, node, node.getParamList());"), code);
    assertFalse(code.contains("ASTMethod node"), code);
    assertTrue(Log.getFindings().isEmpty());
  }
}
