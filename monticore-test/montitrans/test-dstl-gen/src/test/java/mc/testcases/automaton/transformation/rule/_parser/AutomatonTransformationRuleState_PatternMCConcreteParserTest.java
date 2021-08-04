/* (c) https://github.com/MontiCore/monticore */
package mc.testcases.automaton.transformation.rule._parser;

import mc.testcases.automaton.tr.automatontr._ast.ASTState_Pat;
import mc.testcases.automaton.tr.automatontr._parser.AutomatonTRParser;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AutomatonTransformationRuleState_PatternMCConcreteParserTest {

  @Test
  public void testParse1() throws IOException {
    String input = "state s1 { State $BAR }";
    parseStatePattern(input);
  }

  @Test
  public void testParse2() {
    String input = "state s1 { c -y> d; }";
    parseStatePattern(input);
  }

  @Test
  public void testParse3() {
    String input = "state s1 { [[ c -y> d; :- ]] }";
    parseStatePattern(input);
  }

  protected void parseStatePattern(String input) {
    AutomatonTRParser parser = new AutomatonTRParser();

    try {
      Optional<ASTState_Pat> ast = parser.parse_StringState_Pat(input);
      assertFalse(parser.hasErrors());
      assertTrue(ast.isPresent());
    }
    catch (IOException e) {
      fail(e.toString());
    }
  }

}
