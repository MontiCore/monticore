/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.monticore.grammar.grammar_withconcepts._parser.Grammar_WithConceptsParser;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static de.monticore.grammar.cocos.GrammarInheritanceCycle.ERROR_CODE;
import static de.se_rwth.commons.logging.Log.getFindings;
import static de.se_rwth.commons.logging.LogStub.enableFailQuick;
import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import de.se_rwth.commons.logging.Log;

public class GrammarInheritanceCycleTest extends CocoTest {

  private final String MESSAGE = " The grammar A4023%s introduces an inheritance cycle.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @BeforeClass
  public static void disableFailQuick() {
    checker.addCoCo(new GrammarInheritanceCycle());
  }

  @Test
  public void testInvalid() throws IOException {
    Grammar_WithConceptsParser parser = new Grammar_WithConceptsParser();
    Optional<ASTMCGrammar> grammar = parser.parse("src/test/resources/de/monticore/grammar/cocos/invalid/A4023/A4023.mc4");

    getFindings().clear();
    checker.checkAll(grammar.get());

    assertFalse(getFindings().isEmpty());
    assertEquals(1, getFindings().size());
    for (Finding f : getFindings()) {
      assertEquals(format(ERROR_CODE + MESSAGE, ""), f.getMsg());
    }
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
