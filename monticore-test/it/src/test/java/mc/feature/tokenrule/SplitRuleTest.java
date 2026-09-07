/* (c) https://github.com/MontiCore/monticore */

package mc.feature.tokenrule;

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.tokenrule.splitrule.SplitRuleMill;
import mc.feature.tokenrule.splitrule._ast.ASTD;
import mc.feature.tokenrule.splitrule._ast.ASTF;
import mc.feature.tokenrule.splitrule._parser.SplitRuleParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(SplitRuleMill.class)
public class SplitRuleTest {
  
  @Test
  public void test() throws IOException {
    SplitRuleParser parser = SplitRuleMill.parser();
    parser.parse_StringA("::: Foo");
    assertFalse(parser.hasErrors());
    parser.parse_StringA(": :: Foo");
    assertTrue(parser.hasErrors());
    MCAssertions.assertHasFindingStartingWith("rule coloncoloncolon failed predicate: {noSpace(2, 3)}?");
    parser.parse_StringA(": Foo");
    assertTrue(parser.hasErrors());
    MCAssertions.assertHasFindingStartingWith("rule coloncoloncolon failed predicate: {noSpace(2, 3)}?");
    parser.parse_StringB("::: ::: Foo");
    assertFalse(parser.hasErrors());
    Optional<ASTD> ast = parser.parse_StringD(":::");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertEquals(":::", ast.get().getFoo());
    Optional<ASTF> astg = parser.parse_StringF(":::");
    assertFalse(parser.hasErrors());
    assertTrue(astg.isPresent());
    assertTrue(astg.get().isFoo());
  }
  
}
