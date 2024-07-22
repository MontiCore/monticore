/* (c) https://github.com/MontiCore/monticore */

package mc.feature.keyrule;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.GeneratorIntegrationsTest;
import mc.feature.keyrule.nokeywordrule._ast.ASTB;
import mc.feature.keyrule.nokeywordrule._ast.ASTJ;
import mc.feature.keyrule.nokeywordrule._ast.ASTK;
import mc.feature.keyrule.nokeywordrule._parser.NoKeywordRuleParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

public class NoKeywordRuleTest extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void test() throws IOException {
    NoKeywordRuleParser parser = new NoKeywordRuleParser();
    parser.parse_StringA("bla1 bla1");
    Assertions.assertFalse(parser.hasErrors());
    parser.parse_StringA("bla2 bla1");
    Assertions.assertFalse(parser.hasErrors());
    parser.parse_StringA("bla3 bla1");
    Assertions.assertTrue(parser.hasErrors());
    Optional<ASTB> ast = parser.parse_StringB("bla1 bla1");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertEquals("bla1", ast.get().getBla());
    Optional<ASTJ> astj = parser.parse_StringJ("blaj");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(astj.isPresent());
    astj = parser.parse_StringJ("blax");
    Assertions.assertTrue(parser.hasErrors());
    Optional<ASTK> astk = parser.parse_StringK("bla1");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(astk.isPresent());
  }
  
}
