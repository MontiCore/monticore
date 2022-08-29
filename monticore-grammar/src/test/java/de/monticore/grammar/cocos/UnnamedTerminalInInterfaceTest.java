/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import de.se_rwth.commons.logging.Log;

public class UnnamedTerminalInInterfaceTest extends CocoTest  {


  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "de.monticore.grammar.cocos.invalid.A0120.A0120";
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @BeforeClass
  public static void disableFailQuick() {
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
