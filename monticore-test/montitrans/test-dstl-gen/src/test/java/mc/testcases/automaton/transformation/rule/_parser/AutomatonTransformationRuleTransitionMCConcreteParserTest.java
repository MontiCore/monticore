/* (c) https://github.com/MontiCore/monticore */
package mc.testcases.automaton.transformation.rule._parser;

import de.se_rwth.commons.logging.LogStub;
import mc.testcases.automaton.tr.automatontr._parser.AutomatonTRParser;

import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AutomatonTransformationRuleTransitionMCConcreteParserTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testParse2() {
    String input = "[[ d -y> d; :- ]]";
    AutomatonTRParser p =new AutomatonTRParser();
    p.parse_StringITFTransition(input);
    assertFalse(p.hasErrors());

    assertTrue(Log.getFindings().isEmpty());
  }

}
