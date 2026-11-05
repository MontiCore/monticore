/* (c) https://github.com/MontiCore/monticore */
package mc.tfcs;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.testcases.automaton.tr.automatontr.AutomatonTRMill;
import mc.testcases.automaton.tr.automatontr._ast.ASTAutomatonTFRule;
import mc.testcases.automaton.tr.automatontr._ast.ASTITFAutomaton;
import mc.testcases.automaton.tr.automatontr._parser.AutomatonTRParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(AutomatonTRMill.class)
public class TransformationRuleParserTest {

  @Test
  public void testRule() throws  IOException {
    String inputFile = "src/test/resources/SimpleRule.mtr";
    AutomatonTRParser parser = AutomatonTRMill.parser();

    Optional<ASTAutomatonTFRule> ast = parser.parse(inputFile);

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());

    ASTAutomatonTFRule o = ast.get();
    assertNotNull(o);
    assertEquals(1, o.getTFRule().getITFPartList().size());
    ASTITFAutomaton a = (ASTITFAutomaton) o.getTFRule().getITFPartList().get(0);

    assertNotNull(a);
  }
}
