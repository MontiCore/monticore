/* (c) https://github.com/MontiCore/monticore */
package mc.testcases.automaton.transformation.rule._parser;

import mc.testcases.automaton.tr.automatontr._ast.ASTTransition_Pat;
import mc.testcases.automaton.tr.automatontr._parser.AutomatonTRParser;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class AutomatonTransformationRuleTransistion_PatternMCConcreteParserTest {

  @Test
  public void testParse() {
    Optional<ASTTransition_Pat> transitionPattern = Optional.empty();
    String input = "Transition $T [[ $from-$activate>$to; ]]";
    AutomatonTRParser p = new AutomatonTRParser();
    try {
      transitionPattern = p.parse_StringTransition_Pat(input);
      assertFalse(p.hasErrors());
    } catch (IOException e) {
      fail(e.toString());
    }

    assertTrue(transitionPattern.isPresent());
    assertEquals("$T", transitionPattern.get().getSchemaVarName());
  }

}
