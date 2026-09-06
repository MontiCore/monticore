/* (c) https://github.com/MontiCore/monticore */
package mc.tfcs;

import de.se_rwth.commons.logging.LogStub;
import mc.testcases.automaton.tr.automatontr._ast.ASTITFAutomaton;
import mc.testcases.automaton.tr.automatontr._ast.ASTAutomatonTFRule;
import mc.testcases.automaton.tr.automatontr._parser.AutomatonTRParser;

import java.io.IOException;
import java.util.Optional;

import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TransformationRuleParserTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testRule() throws  IOException {
    String inputFile = "src/test/resources/SimpleRule.mtr";
    AutomatonTRParser parser = new AutomatonTRParser();

    Optional<ASTAutomatonTFRule> ast = parser.parse(inputFile);

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());

    ASTAutomatonTFRule o = ast.get();
    assertNotNull(o);
    assertEquals(1, o.getTFRule().getITFPartList().size());
    ASTITFAutomaton a = (ASTITFAutomaton) o.getTFRule().getITFPartList().get(0);

    assertNotNull(a);
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
