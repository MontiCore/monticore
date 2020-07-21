/* (c) https://github.com/MontiCore/monticore */

package mc.feature.tokenrule;

import mc.GeneratorIntegrationsTest;
import mc.feature.tokenrule.tokenrule._ast.ASTC;
import mc.feature.tokenrule.tokenrule._ast.ASTG;
import mc.feature.tokenrule.tokenrule._parser.TokenRuleParser;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class TokenRuleTest extends GeneratorIntegrationsTest {
  
  @Test
  public void test() throws IOException {
    TokenRuleParser parser = new TokenRuleParser();
    parser.parse_StringA(":: Foo");
    assertFalse(parser.hasErrors());
    parser.parse_StringA(": : Foo");
    assertTrue(parser.hasErrors());
    parser.parse_StringA(": Foo");
    assertTrue(parser.hasErrors());
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
