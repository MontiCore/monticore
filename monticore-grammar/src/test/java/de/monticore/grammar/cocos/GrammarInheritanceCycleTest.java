/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.monticore.grammar.grammar_withconcepts._parser.Grammar_WithConceptsParser;
import de.monticore.runtime.junit.MCAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static de.monticore.grammar.cocos.GrammarInheritanceCycle.ERROR_CODE;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GrammarInheritanceCycleTest extends CocoTest {

  private final String MESSAGE = " The grammar A4023%s introduces an inheritance cycle.";
  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new GrammarInheritanceCycle());
  }

  @Test
  public void testInvalid() throws IOException {
    Grammar_WithConceptsParser parser = new Grammar_WithConceptsParser();
    Optional<ASTMCGrammar> grammar = parser.parse("target/resources/test/de/monticore/grammar/cocos/invalid/A4023/A4023.mc4");
    assertTrue(grammar.isPresent());

    checker.checkAll(grammar.get());
    
    MCAssertions.assertHasFindingStartingWith(ERROR_CODE + MESSAGE.formatted(""));
  }

  @Test
  public void testCorrect() {
    testValidGrammar("de.monticore.grammar.cocos.valid.Overriding", checker);
  }

  @Test
  public void testCorrectB() {
    testValidGrammar("de.monticore.grammar.cocos.valid.enum.Enum", checker);
  }

}
