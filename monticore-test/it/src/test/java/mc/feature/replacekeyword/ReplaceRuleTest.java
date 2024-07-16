/* (c) https://github.com/MontiCore/monticore */

package mc.feature.replacekeyword;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.GeneratorIntegrationsTest;
import mc.feature.replacerule.replacerule2.ReplaceRule2Mill;
import mc.feature.replacerule.replacerule2._parser.ReplaceRule2Parser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ReplaceRuleTest extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void test() throws IOException {
    ReplaceRule2Parser parser = ReplaceRule2Mill.parser();

    // Replace keyword
    parser.parse_StringA("a1 Foo");
    Assertions.assertFalse(parser.hasErrors());

    parser.parse_StringA("A Foo");
    Assertions.assertTrue(parser.hasErrors());

    // Add keyword in combination with nokeyword
    parser.parse_StringB("BLA Foo");
    Assertions.assertFalse(parser.hasErrors());

    parser.parse_StringB("bla Foo");
    Assertions.assertFalse(parser.hasErrors());

    // Replace keyword in combination with key
    parser.parse_StringC("bla_c Foo");
    Assertions.assertFalse(parser.hasErrors());

    parser.parse_StringC("BLA_C Foo");
    Assertions.assertTrue(parser.hasErrors());

    // Replace keyword in combination with splittoken
    parser.parse_StringD("}} Foo");
    Assertions.assertFalse(parser.hasErrors());

    parser.parse_StringD(">> Foo");
    Assertions.assertTrue(parser.hasErrors());

    // Add keyword in combination with token
    parser.parse_StringE("{{ Foo");
    Assertions.assertFalse(parser.hasErrors());

    parser.parse_StringE("<< Foo");
    Assertions.assertFalse(parser.hasErrors());


  }
  
}
