/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.codegen.mc2cd.TranslationTestCase;
import de.monticore.grammar.DirectLeftRecursionDetector;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

/**
 * Tests the helper class on a concrete grammar containing left recursive and normal rules.
 *
 */
public class DirectLeftRecursionDetectorTest extends TranslationTestCase {

  private Optional<ASTMCGrammar> astMCGrammarOptional;
  
  private DirectLeftRecursionDetector directLeftRecursionDetector = new DirectLeftRecursionDetector();

  @BeforeEach
  public void setup() throws IOException {
    final Path modelPath = Paths.get("src/test/resources/mc2cdtransformation/DirectLeftRecursionDetector.mc4");
    astMCGrammarOptional =Grammar_WithConceptsMill.parser().parse(modelPath.toString());;
    Assertions.assertTrue(astMCGrammarOptional.isPresent());
  }

  @Test
  public void testRecursiveRule() {
    final List<ASTClassProd> productions = astMCGrammarOptional.get().getClassProdList();

    final ASTClassProd exprProduction = productions.get(0);

    boolean isLeftRecursive = directLeftRecursionDetector.isAlternativeLeftRecursive(exprProduction.getAlt(0), exprProduction.getName());
    Assertions.assertTrue(isLeftRecursive);

    final ASTClassProd nonRecursiveProudction1 = productions.get(1);
    isLeftRecursive = directLeftRecursionDetector.isAlternativeLeftRecursive(nonRecursiveProudction1.getAlt(0), nonRecursiveProudction1.getName());
    Assertions.assertFalse(isLeftRecursive);

    final ASTClassProd nonRecursiveProudction2 = productions.get(2);
    isLeftRecursive = directLeftRecursionDetector.isAlternativeLeftRecursive(nonRecursiveProudction2.getAlt(0), nonRecursiveProudction2.getName());
    Assertions.assertFalse(isLeftRecursive);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}
