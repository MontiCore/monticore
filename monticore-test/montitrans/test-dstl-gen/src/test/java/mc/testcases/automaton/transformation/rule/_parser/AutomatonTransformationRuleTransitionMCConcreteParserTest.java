/* (c) https://github.com/MontiCore/monticore */
package mc.testcases.automaton.transformation.rule._parser;

import mc.testcases.automaton.tr.automatontr._parser.AutomatonTRParser;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class AutomatonTransformationRuleTransitionMCConcreteParserTest {



  @Test
  public void testParse2() {
    String input = "[[ d -y> d; :- ]]";
    AutomatonTRParser p =new AutomatonTRParser();
    try {
      p.parse_StringITFTransition(input);
      assertFalse(p.hasErrors());
    } catch (IOException e) {
      fail(e.toString());
    }
  }

}
