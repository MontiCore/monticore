/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UniqueProdNamesForCompTest extends CocoTest {

  private final String grammar = "de.monticore.grammar.cocos.invalid.A0144.A0144";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new UniqueProdNamesForComp());
  }

  @Test
  public void testValidSuperA() {
    testValidGrammar(grammar + "a", checker);
  }

  @Test
  public void testValidSuperB() {
    testValidGrammar(grammar + "b", checker);
  }

  @Test
  public void testValidSuperBB() {
    // This grammar overrides a production
    testValidGrammar(grammar + "bb", checker);
  }


  @Test
  public void testInvalidComposition() {
    // Foo is inherited from both a and b
    testInvalidGrammar(grammar + "c", UniqueProdNamesForComp.ERROR_CODE,
            " The nonterminal Foo is inherited from conflicting grammars: de.monticore.grammar.cocos.invalid.A0144.A0144a, de.monticore.grammar.cocos.invalid.A0144.A0144b.",
            checker);
  }

  @Test
  public void testInvalidCompositiond() {
    // Foo is inherited from both a and b/bb
    testInvalidGrammar(grammar + "d", UniqueProdNamesForComp.ERROR_CODE,
            " The nonterminal Foo is inherited from conflicting grammars: de.monticore.grammar.cocos.invalid.A0144.A0144a, de.monticore.grammar.cocos.invalid.A0144.A0144bb.",
            checker);
  }


}
