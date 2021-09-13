/* (c) https://github.com/MontiCore/monticore */
package mc.testcases.automaton.transformation.rule._parser;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.IOException;

import mc.testcases.automaton.tr.automatontr._parser.AutomatonTRParser;
import org.junit.Test;

public class AutomatonTransformationRuleTransition_ReplacementMCConcreteParserTest {

  @Test
  public void testParse() {
    String input = "[[ d -y> d; :- ]]";
    AutomatonTRParser p = new AutomatonTRParser();
    try {
      p.parse_StringTransition_Rep(input);
      assertFalse(p.hasErrors());
    } catch (IOException e) {
      fail(e.toString());
    }
  }
}
