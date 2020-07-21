/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

public class UnnamedTerminalInInterfaceTest extends CocoTest  {


  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A0120.A0120";

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.enableFailQuick(false);
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
    testValidGrammar("cocos.valid.ImplementInterfaceNTs", checker);
  }

}
