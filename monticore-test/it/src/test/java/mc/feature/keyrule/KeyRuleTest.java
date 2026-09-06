/* (c) https://github.com/MontiCore/monticore */

package mc.feature.keyrule;

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.keyrule.keyrule.KeyRuleMill;
import mc.feature.keyrule.keyrule._ast.ASTB;
import mc.feature.keyrule.keyrule._ast.ASTJ;
import mc.feature.keyrule.keyrule._parser.KeyRuleParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(KeyRuleMill.class)
public class KeyRuleTest {

  @Test
  public void test() throws IOException {
    KeyRuleParser parser = KeyRuleMill.parser();
    parser.parse_StringA("bla1 Foo");
    assertFalse(parser.hasErrors());
    parser.parse_StringA("bla2 Foo");
    assertFalse(parser.hasErrors());
    parser.parse_StringA("bla3 Foo");
    assertTrue(parser.hasErrors());
    MCAssertions.assertHasFindingStartingWith(
        "no viable alternative at input 'bla3', expecting 'bla1' or 'bla2'");
    Optional<ASTB> ast = parser.parse_StringB("bla1 Foo");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertEquals("bla1", ast.get().getBla());
    ast = parser.parse_StringB("bla2 Foo");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertEquals("bla2", ast.get().getBla());
    Optional<ASTJ> astj = parser.parse_StringJ("blaj");
    assertFalse(parser.hasErrors());
    assertTrue(astj.isPresent());
    astj = parser.parse_StringJ("blax");
    assertTrue(parser.hasErrors());
    assertFalse(astj.isPresent());
    MCAssertions.assertHasFindingStartingWith("mismatched input 'blax', expecting 'blaj'");
  }
  
}
