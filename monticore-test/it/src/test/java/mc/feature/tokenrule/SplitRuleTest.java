/* (c) https://github.com/MontiCore/monticore */

package mc.feature.tokenrule;

import de.se_rwth.commons.logging.LogStub;
import mc.GeneratorIntegrationsTest;
import mc.feature.tokenrule.splitrule._ast.ASTD;
import mc.feature.tokenrule.splitrule._ast.ASTF;
import mc.feature.tokenrule.splitrule._parser.SplitRuleParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import de.se_rwth.commons.logging.Log;

public class SplitRuleTest extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void test() throws IOException {
    SplitRuleParser parser = new SplitRuleParser();
    parser.parse_StringA("::: Foo");
    Assertions.assertFalse(parser.hasErrors());
    parser.parse_StringA(": :: Foo");
    Assertions.assertTrue(parser.hasErrors());
    parser.parse_StringA(": Foo");
    Assertions.assertTrue(parser.hasErrors());
    parser.parse_StringB("::: ::: Foo");
    Assertions.assertFalse(parser.hasErrors());
    Optional<ASTD> ast = parser.parse_StringD(":::");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertEquals(":::", ast.get().getFoo());
    Optional<ASTF> astg = parser.parse_StringF(":::");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(astg.isPresent());
    Assertions.assertTrue(astg.get().isFoo());
  }
  
}
