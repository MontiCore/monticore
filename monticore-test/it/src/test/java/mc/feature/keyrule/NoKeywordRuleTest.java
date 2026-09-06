/* (c) https://github.com/MontiCore/monticore */

package mc.feature.keyrule;

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.keyrule.nokeywordrule.NoKeywordRuleMill;
import mc.feature.keyrule.nokeywordrule._ast.ASTB;
import mc.feature.keyrule.nokeywordrule._ast.ASTJ;
import mc.feature.keyrule.nokeywordrule._ast.ASTK;
import mc.feature.keyrule.nokeywordrule._parser.NoKeywordRuleParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(NoKeywordRuleMill.class)
public class NoKeywordRuleTest {

  @Test
  public void test() throws IOException {
    NoKeywordRuleParser parser = NoKeywordRuleMill.parser();
    parser.parse_StringA("bla1 bla1");
    assertFalse(parser.hasErrors());
    parser.parse_StringA("bla2 bla1");
    assertFalse(parser.hasErrors());
    parser.parse_StringA("bla3 bla1");
    assertTrue(parser.hasErrors());
    MCAssertions.assertHasFindingStartingWith(
        "no viable alternative at input 'bla3', expecting 'bla1' or 'bla2'");
    Optional<ASTB> ast = parser.parse_StringB("bla1 bla1");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertEquals("bla1", ast.get().getBla());
    Optional<ASTJ> astj = parser.parse_StringJ("blaj");
    assertFalse(parser.hasErrors());
    assertTrue(astj.isPresent());
    astj = parser.parse_StringJ("blax");
    assertTrue(parser.hasErrors());
    assertFalse(astj.isPresent());
    MCAssertions.assertHasFindingStartingWith("mismatched input 'blax', expecting 'blaj'");
    Optional<ASTK> astk = parser.parse_StringK("bla1");
    assertFalse(parser.hasErrors());
    assertTrue(astk.isPresent());
  }
  
}
