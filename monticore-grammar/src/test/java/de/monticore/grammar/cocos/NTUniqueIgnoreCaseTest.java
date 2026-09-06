/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NTUniqueIgnoreCaseTest extends CocoTest {
  
  private final String MESSAGE = " The nonterminal A must not be defined by more than one production: nonterminals aren't case-sensitive.";
  
  private final String grammar = "de.monticore.grammar.cocos.invalid.A2026.A2026";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new NTUniqueIgnoreCase());
  }
  
  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, NTUniqueIgnoreCase.ERROR_CODE, MESSAGE, checker);
  }
  
}
