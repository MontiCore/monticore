/* (c) https://github.com/MontiCore/monticore */

package mc.feature.abc;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.abc.realabc.RealABCMill;
import mc.feature.abc.realabc._parser.RealABCParser;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;

@TestWithMCLanguage(RealABCMill.class)
public class EmbedTest {

  @ParameterizedTest
  @ValueSource( strings = {
      "a b c",
      "a b",
      "a a a b b b c c c",
      "a b c c"
  })
  public void parse(String in) throws IOException {
    RealABCParser parser = RealABCMill.parser();
    parser.parse_StringS(in);
    
    assertFalse(parser.hasErrors());
  }
  
}
