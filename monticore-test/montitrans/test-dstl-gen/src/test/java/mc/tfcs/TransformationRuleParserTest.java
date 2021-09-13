/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import junit.framework.TestCase;
import mc.testcases.automaton.tr.automatontr._ast.ASTITFAutomaton;
import mc.testcases.automaton.tr.automatontr._ast.ASTAutomatonTFRule;
import mc.testcases.automaton.tr.automatontr._parser.AutomatonTRParser;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

public class TransformationRuleParserTest extends TestCase {
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
  }
}
