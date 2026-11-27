/* (c) https://github.com/MontiCore/monticore */
package mc.testcases.automaton.transformation.rule._parser;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.testcases.automaton.tr.automatontr.AutomatonTRMill;
import mc.testcases.automaton.tr.automatontr._parser.AutomatonTRParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;

@TestWithMCLanguage(AutomatonTRMill.class)
public class AutomatonTransformationRuleTransition_ReplacementMCConcreteParserTest {

  @Test
  public void testParse() throws IOException {
    String input = "[[ d -y> d; :- ]]";
    AutomatonTRParser p = AutomatonTRMill.parser();
    p.parse_StringTransition_Rep(input);
    assertFalse(p.hasErrors());
  }
}
