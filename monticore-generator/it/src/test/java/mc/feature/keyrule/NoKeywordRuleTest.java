/* (c) https://github.com/MontiCore/monticore */

package mc.feature.keyrule;

import mc.GeneratorIntegrationsTest;
import mc.feature.keyrule.nokeywordrule._ast.ASTB;
import mc.feature.keyrule.nokeywordrule._ast.ASTJ;
import mc.feature.keyrule.nokeywordrule._parser.NoKeywordRuleParser;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class NoKeywordRuleTest extends GeneratorIntegrationsTest {
  
  @Test
  public void test() throws IOException {
    NoKeywordRuleParser parser = new NoKeywordRuleParser();
    parser.parse_StringA("bla1 Foo");
    assertFalse(parser.hasErrors());
    parser.parse_StringA("bla2 Foo");
    assertFalse(parser.hasErrors());
    parser.parse_StringA("bla3 Foo");
    assertTrue(parser.hasErrors());
    Optional<ASTB> ast = parser.parse_StringB("bla1 Foo");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertEquals("bla1", ast.get().getBla());
    Optional<ASTJ> astj = parser.parse_StringJ("blaj");
    assertFalse(parser.hasErrors());
    assertTrue(astj.isPresent());
    astj = parser.parse_StringJ("blax");
    assertTrue(parser.hasErrors());
  }
  
}
