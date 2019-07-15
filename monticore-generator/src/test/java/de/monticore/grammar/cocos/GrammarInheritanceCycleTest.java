/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;
import parser.MCGrammarParser;

import java.nio.file.Paths;
import java.util.Optional;

import static de.monticore.grammar.cocos.GrammarInheritanceCycle.ERROR_CODE;
import static de.se_rwth.commons.logging.LogStub.enableFailQuick;
import static de.se_rwth.commons.logging.Log.getFindings;
import static java.lang.String.format;
import static java.nio.file.Paths.get;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static parser.MCGrammarParser.parse;

public class GrammarInheritanceCycleTest extends CocoTest {

  private final String MESSAGE = " The grammar A4023%s introduces an inheritance cycle.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A4023.A4023";

  @BeforeClass
  public static void disableFailQuick() {
    enableFailQuick(false);
    checker.addCoCo(new GrammarInheritanceCycle());
  }

  @Test
  public void testInvalid() {
    Optional<ASTMCGrammar> grammar = parse(get("src/test/resources/cocos/invalid/A4023/A4023.mc4"));

    getFindings().clear();
    checker.handle(grammar.get());

    assertFalse(getFindings().isEmpty());
    assertEquals(1, getFindings().size());
    for (Finding f : getFindings()) {
      assertEquals(format(ERROR_CODE + MESSAGE, ""), f.getMsg());
    }
  }

  @Test
  public void testCorrect() {
    testValidGrammar("cocos.valid.Overriding", checker);
  }

  @Test
  public void testCorrectB() {
    testValidGrammar("cocos.valid.enum.Enum", checker);
  }

}
