/* (c) https://github.com/MontiCore/monticore */

package mc.feature.tokenrule;

import de.se_rwth.commons.logging.LogStub;
import mc.GeneratorIntegrationsTest;
import mc.feature.tokenrule.tokenrule._ast.ASTC;
import mc.feature.tokenrule.tokenrule._ast.ASTG;
import mc.feature.tokenrule.tokenrule._parser.TokenRuleParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import de.se_rwth.commons.logging.Log;

public class TokenRuleTest extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void test() throws IOException {
    TokenRuleParser parser = new TokenRuleParser();
    parser.parse_StringA(":: Foo");
    Assertions.assertFalse(parser.hasErrors());
    parser.parse_StringA(": : Foo");
    Assertions.assertTrue(parser.hasErrors());
    parser.parse_StringA(": Foo");
    Assertions.assertTrue(parser.hasErrors());
    parser.parse_StringB("::: Foo");
    Assertions.assertFalse(parser.hasErrors());
    Optional<ASTC> ast = parser.parse_StringC(":: Foo");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertEquals("::", ast.get().getX());
    Optional<ASTG> astg = parser.parse_StringG("::");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(astg.isPresent());
    Assertions.assertTrue(astg.get().isY());
  }
  
}
