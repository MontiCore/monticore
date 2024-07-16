/* (c) https://github.com/MontiCore/monticore */

package mc.feature.keyrule;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.GeneratorIntegrationsTest;
import mc.feature.keyrule.keyrule._ast.ASTB;
import mc.feature.keyrule.keyrule._ast.ASTJ;
import mc.feature.keyrule.keyrule._parser.KeyRuleParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class KeyRuleTest extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void test() throws IOException {
    KeyRuleParser parser = new KeyRuleParser();
    parser.parse_StringA("bla1 Foo");
    Assertions.assertFalse(parser.hasErrors());
    parser.parse_StringA("bla2 Foo");
    Assertions.assertFalse(parser.hasErrors());
    parser.parse_StringA("bla3 Foo");
    Assertions.assertTrue(parser.hasErrors());
    Optional<ASTB> ast = parser.parse_StringB("bla1 Foo");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertEquals("bla1", ast.get().getBla());
    ast = parser.parse_StringB("bla2 Foo");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertEquals("bla2", ast.get().getBla());
    Optional<ASTJ> astj = parser.parse_StringJ("blaj");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(astj.isPresent());
    astj = parser.parse_StringJ("blax");
    Assertions.assertTrue(parser.hasErrors());
  }
  
}
