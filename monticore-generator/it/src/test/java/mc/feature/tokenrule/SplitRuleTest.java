/* (c) https://github.com/MontiCore/monticore */

package mc.feature.tokenrule;

import mc.GeneratorIntegrationsTest;
import mc.feature.tokenrule.splitrule._ast.ASTD;
import mc.feature.tokenrule.splitrule._ast.ASTF;
import mc.feature.tokenrule.splitrule._parser.SplitRuleParser;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class SplitRuleTest extends GeneratorIntegrationsTest {
  
  @Test
  public void test() throws IOException {
    SplitRuleParser parser = new SplitRuleParser();
    parser.parse_StringA("::: Foo");
    assertFalse(parser.hasErrors());
    parser.parse_StringA(": :: Foo");
    assertTrue(parser.hasErrors());
    parser.parse_StringA(": Foo");
    assertTrue(parser.hasErrors());
    parser.parse_StringB("::: ::: Foo");
    assertFalse(parser.hasErrors());
    Optional<ASTD> ast = parser.parse_StringD("::: Foo");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertEquals(":::", ast.get().getFoo());
    Optional<ASTF> astg = parser.parse_StringF(":::");
    assertFalse(parser.hasErrors());
    assertTrue(astg.isPresent());
    assertTrue(astg.get().isFoo());
  }
  
}
