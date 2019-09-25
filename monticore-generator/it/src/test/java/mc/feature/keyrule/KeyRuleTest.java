/* (c) https://github.com/MontiCore/monticore */

package mc.feature.keyrule;

import mc.GeneratorIntegrationsTest;
import mc.feature.aststring.aststring._ast.ASTStart;
import mc.feature.aststring.aststring._parser.AststringParser;
import mc.feature.keyrule.keyrule._ast.ASTA;
import mc.feature.keyrule.keyrule._ast.ASTB;
import mc.feature.keyrule.keyrule._parser.KeyRuleParser;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class KeyRuleTest extends GeneratorIntegrationsTest {
  
  @Test
  public void test() throws IOException {
    KeyRuleParser parser = new KeyRuleParser();
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
    ast = parser.parse_StringB("bla2 Foo");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertEquals("bla2", ast.get().getBla());
  }
  
}
