/* (c) https://github.com/MontiCore/monticore */
package mc.testcases.automaton.transformation.rule._parser;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.testcases.automaton.tr.automatontr.AutomatonTRMill;
import mc.testcases.automaton.tr.automatontr._ast.ASTState_Pat;
import mc.testcases.automaton.tr.automatontr._parser.AutomatonTRParser;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(AutomatonTRMill.class)
public class AutomatonTransformationRuleState_PatternMCConcreteParserTest {
  
  @ParameterizedTest
  @ValueSource(strings = {
      "state s1 { State $BAR }",
      "state s1 { c -y> d; }",
      "state s1 { [[ c -y> d; :- ]] }"
  })
  public void testStatePattern(String input) throws IOException {
    AutomatonTRParser parser = AutomatonTRMill.parser();
    
    Optional<ASTState_Pat> ast = parser.parse_StringState_Pat(input);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

}
