/* (c) https://github.com/MontiCore/monticore */
package mc.testcases.automaton.transformation.rule._parser;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.testcases.automaton.tr.automatontr.AutomatonTRMill;
import mc.testcases.automaton.tr.automatontr._ast.ASTTransition_Pat;
import mc.testcases.automaton.tr.automatontr._parser.AutomatonTRParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(AutomatonTRMill.class)
public class AutomatonTransformationRuleTransistion_PatternMCConcreteParserTest {

  @Test
  public void testParse() throws IOException {
    String input = "Transition $T [[ $from-$activate>$to; ]]";
    AutomatonTRParser p = AutomatonTRMill.parser();
    
    Optional<ASTTransition_Pat> transitionPattern = p.parse_StringTransition_Pat(input);
    assertFalse(p.hasErrors());
    assertTrue(transitionPattern.isPresent());
    
    assertEquals("$T", transitionPattern.get().getSchemaVarName());
  }

}
