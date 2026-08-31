/* (c) https://github.com/MontiCore/monticore */
package mc.testcases.automaton.transformation.rule._parser;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.testcases.automaton.tr.automatontr.AutomatonTRMill;
import mc.testcases.automaton.tr.automatontr._ast.ASTTransition_Rep;
import mc.testcases.automaton.tr.automatontr._parser.AutomatonTRParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(AutomatonTRMill.class)
public class AutomatonTransformationRuleTransition_ReplacementMCConcreteParserTest {

  @Test
  public void testParse() throws IOException {
    String input = "[[ d -y> d; :- ]]";
    AutomatonTRParser p = AutomatonTRMill.parser();
    
    Optional<ASTTransition_Rep> transitionOpt = p.parse_StringTransition_Rep(input);
    assertTrue(transitionOpt.isPresent());
    assertFalse(p.hasErrors());
  }
}
