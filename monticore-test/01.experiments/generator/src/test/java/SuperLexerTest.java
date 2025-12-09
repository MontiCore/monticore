/* (c) https://github.com/MontiCore/monticore */

import de.monticore.runtime.junit.TestWithMCLanguage;
import org.junit.jupiter.api.Test;
import superlexer.ModifiedMCLexerBase;
import superlexer.SuperLexerMill;
import superlexer._parser.SuperLexerParser;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Test that the LexerSuperGrammar AntlrOption works
 */
@TestWithMCLanguage(SuperLexerMill.class)
public class SuperLexerTest {

  @Test
  public void test() throws IOException {
    ModifiedMCLexerBase.lexCalled = 0;

    SuperLexerParser parser = SuperLexerMill.parser();

    parser.parse_String("a;");

    assertFalse(parser.hasErrors());
    assertEquals(1, ModifiedMCLexerBase.lexCalled);
  }
}
