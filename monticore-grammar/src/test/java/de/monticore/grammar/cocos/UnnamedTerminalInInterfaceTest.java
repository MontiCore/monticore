/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UnnamedTerminalInInterfaceTest extends CocoTest  {
  private final String grammar = "de.monticore.grammar.cocos.invalid.A0120.A0120";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new UnnamedTerminalInInterface());
  }

  @Test
  public void testInvalidTerminal() {
    testInvalidGrammar(grammar, UnnamedTerminalInInterface.ERROR_CODE,
        String.format(UnnamedTerminalInInterface.ERROR_MSG_FORMAT, "Foo", "Terminal", "b"), checker);
  }

  @Test
  public void testInvalidKeyTerminal() {
    testInvalidGrammar(grammar+ "b", UnnamedTerminalInInterface.ERROR_CODE,
        String.format(UnnamedTerminalInInterface.ERROR_MSG_FORMAT, "Foo", "KeyTerminal", "b"), checker);
  }


  @Test
  public void testCorrect() {
    testValidGrammar("de.monticore.grammar.cocos.valid.ImplementInterfaceNTs", checker);
  }

}
