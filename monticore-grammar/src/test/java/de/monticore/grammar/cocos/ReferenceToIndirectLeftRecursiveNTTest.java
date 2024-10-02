/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ReferenceToIndirectLeftRecursiveNTTest extends CocoTest {
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4060.A4060";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new ReferenceToIndirectLeftRecursiveNT ());
  }

  @Test
  public void testSimple() {
    testInvalidGrammar(grammar, ReferenceToIndirectLeftRecursiveNT.ERROR_CODE,
        String.format(ReferenceToIndirectLeftRecursiveNT.ERROR_MSG_FORMAT, "A"), checker);
  }

  @Test
  public void testNTSeparator() {
    testInvalidGrammar(grammar+"a", ReferenceToIndirectLeftRecursiveNT.ERROR_CODE,
        String.format(ReferenceToIndirectLeftRecursiveNT.ERROR_MSG_FORMAT, "A"), checker, 2);
  }

  @Test
  public void testCorrect(){
    testValidGrammar("de.monticore.grammar.cocos.valid.Expressions", checker);
  }

}
