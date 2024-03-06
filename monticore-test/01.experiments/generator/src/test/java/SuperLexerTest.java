/* (c) https://github.com/MontiCore/monticore */

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;
import superlexer.ModifiedMCLexerBase;
import superlexer.SuperLexerMill;
import superlexer._parser.SuperLexerParser;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Test that the LexerSuperGrammar AntlrOption works
 */
public class SuperLexerTest {
  
  @Before
  public void setup() {
    LogStub.init();         // replace log by a sideffect free variant
    Log.enableFailQuick(false);
  }

  @Test
  public void test() throws IOException {
    ModifiedMCLexerBase.lexCalled = 0;
    SuperLexerMill.init();

    SuperLexerParser parser = SuperLexerMill.parser();

    parser.parse_String("a;");

    assertFalse(parser.hasErrors());
    assertEquals(1, ModifiedMCLexerBase.lexCalled);
  }
}
