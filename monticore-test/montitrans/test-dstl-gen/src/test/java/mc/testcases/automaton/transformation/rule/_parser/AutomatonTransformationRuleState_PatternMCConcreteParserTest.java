/* (c) https://github.com/MontiCore/monticore */
package mc.testcases.automaton.transformation.rule._parser;

import de.se_rwth.commons.logging.LogStub;
import mc.testcases.automaton.tr.automatontr._ast.ASTState_Pat;
import mc.testcases.automaton.tr.automatontr._parser.AutomatonTRParser;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import de.se_rwth.commons.logging.Log;

public class AutomatonTransformationRuleState_PatternMCConcreteParserTest {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testParse1() throws IOException {
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
