/* (c) https://github.com/MontiCore/monticore */
package mc.testcases.automaton.transformation.rule._parser;

import de.se_rwth.commons.logging.LogStub;
import mc.testcases.automaton.tr.automatontr._ast.ASTState_Pat;
import mc.testcases.automaton.tr.automatontr._parser.AutomatonTRParser;

import java.util.Optional;

import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AutomatonTransformationRuleState_PatternMCConcreteParserTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testParse1() {
    String input = "state s1 { State $BAR }";
    parseStatePattern(input);
    
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testParse2() {
    String input = "state s1 { c -y> d; }";
    parseStatePattern(input);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testParse3() {
    String input = "state s1 { [[ c -y> d; :- ]] }";
    parseStatePattern(input);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  protected void parseStatePattern(String input) {
    AutomatonTRParser parser = new AutomatonTRParser();

    Optional<ASTState_Pat> ast = parser.parse_StringState_Pat(input);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

}
