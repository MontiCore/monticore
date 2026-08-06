/* (c) https://github.com/MontiCore/monticore */

package mc.feature.tokenrule;

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.tokenrule.tokenrule.TokenRuleMill;
import mc.feature.tokenrule.tokenrule._ast.ASTC;
import mc.feature.tokenrule.tokenrule._ast.ASTG;
import mc.feature.tokenrule.tokenrule._parser.TokenRuleParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(TokenRuleMill.class)
public class TokenRuleTest {
  
  @Test
  public void test() throws IOException {
    TokenRuleParser parser = TokenRuleMill.parser();
    parser.parse_StringA(":: Foo");
    assertFalse(parser.hasErrors());
    parser.parse_StringA(": : Foo");
    assertTrue(parser.hasErrors());
    MCAssertions.assertHasFindingStartingWith("rule coloncolon failed predicate: {noSpace(2)}?");
    parser.parse_StringA(": Foo");
    assertTrue(parser.hasErrors());
    MCAssertions.assertHasFindingStartingWith("rule coloncolon failed predicate: {noSpace(2)}?");
    parser.parse_StringB("::: Foo");
    assertFalse(parser.hasErrors());
    Optional<ASTC> ast = parser.parse_StringC(":: Foo");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertEquals("::", ast.get().getX());
    Optional<ASTG> astg = parser.parse_StringG("::");
    assertFalse(parser.hasErrors());
    assertTrue(astg.isPresent());
    assertTrue(astg.get().isY());
  }
  
}
