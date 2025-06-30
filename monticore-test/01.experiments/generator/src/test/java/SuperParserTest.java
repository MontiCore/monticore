/* (c) https://github.com/MontiCore/monticore */

import de.monticore.runtime.junit.TestWithMCLanguage;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import superparser.ModifiedMCParserBase;
import superparser.SuperParserMill;
import superparser._parser.SuperParserParser;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 *  Test that the ParserSuperGrammar AntlrOption works
 */
@TestWithMCLanguage(SuperParserMill.class)
public class SuperParserTest {
  

  @Test
  public void test() throws IOException {
    ModifiedMCParserBase.customCalled = 0;

    SuperParserParser parser = SuperParserMill.parser();

    parser.parse_String("...");

    assertFalse(parser.hasErrors());
    assertEquals(1, ModifiedMCParserBase.customCalled);
  }
}
