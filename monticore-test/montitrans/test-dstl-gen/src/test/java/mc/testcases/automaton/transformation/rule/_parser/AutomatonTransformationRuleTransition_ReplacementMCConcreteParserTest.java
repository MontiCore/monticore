/* (c) https://github.com/MontiCore/monticore */
package mc.testcases.automaton.transformation.rule._parser;

import java.io.IOException;

import de.se_rwth.commons.logging.LogStub;
import mc.testcases.automaton.tr.automatontr._parser.AutomatonTRParser;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;
import de.se_rwth.commons.logging.Log;

public class AutomatonTransformationRuleTransition_ReplacementMCConcreteParserTest {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
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
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
