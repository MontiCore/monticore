/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NTNotExtendInterfaceOrExternalNTsTest extends CocoTest {

  private final String MESSAGE = " The nonterminal B must not extend the %s nonterminal A. " +
          "Nonterminals may only extend abstract or normal nonterminals.";
  private final String grammar = "de.monticore.grammar.cocos.invalid.A2103.A2103";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new NTNotExtendInterfaceOrExternalNTs());
  }

  @Test
  public void testExtendInterfaceNT() {
    testInvalidGrammar(grammar + "a", NTNotExtendInterfaceOrExternalNTs.ERROR_CODE,
        String.format(MESSAGE, "interface"), checker);
  }
  
  @Test
  public void testExtendExternalNT() {
    testInvalidGrammar(grammar + "b", NTNotExtendInterfaceOrExternalNTs.ERROR_CODE,
        String.format(MESSAGE, "external"), checker);
  }

  @Test
  public void testExtendNT(){
    testValidGrammar("de.monticore.grammar.cocos.valid.ExtendNTs", checker);
  }
}
