/* (c) https://github.com/MontiCore/monticore */

package mc.feature.replacekeyword;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.GeneratorIntegrationsTest;
import mc.feature.replacerule.replacerule2.ReplaceRule2Mill;
import mc.feature.replacerule.replacerule2._parser.ReplaceRule2Parser;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ReplaceRuleTest extends GeneratorIntegrationsTest {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void test() throws IOException {
    ReplaceRule2Parser parser = ReplaceRule2Mill.parser();

    // Replace keyword
    parser.parse_StringA("a1 Foo");
    assertFalse(parser.hasErrors());

    parser.parse_StringA("A Foo");
    assertTrue(parser.hasErrors());

    // Add keyword in combination with nokeyword
    parser.parse_StringB("BLA Foo");
    assertFalse(parser.hasErrors());

    parser.parse_StringB("bla Foo");
    assertFalse(parser.hasErrors());

    // Replace keyword in combination with key
    parser.parse_StringC("bla_c Foo");
    assertFalse(parser.hasErrors());

    parser.parse_StringC("BLA_C Foo");
    assertTrue(parser.hasErrors());

    // Replace keyword in combination with splittoken
    parser.parse_StringD("}} Foo");
    assertFalse(parser.hasErrors());

    parser.parse_StringD(">> Foo");
    assertTrue(parser.hasErrors());

    // Add keyword in combination with token
    parser.parse_StringE("{{ Foo");
    assertFalse(parser.hasErrors());

    parser.parse_StringE("<< Foo");
    assertFalse(parser.hasErrors());


  }
  
}
