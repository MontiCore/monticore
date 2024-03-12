/* (c) https://github.com/MontiCore/monticore */

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;
import superparser.ModifiedMCParserBase;
import superparser.SuperParserMill;
import superparser._parser.SuperParserParser;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 *  Test that the ParserSuperGrammar AntlrOption works
 */
public class SuperParserTest {
  
  @Before
  public void setup() {
    LogStub.init();         // replace log by a sideffect free variant
    Log.enableFailQuick(false);
  }

  @Test
  public void test() throws IOException {
    ModifiedMCParserBase.customCalled = 0;
    SuperParserMill.init();

    SuperParserParser parser = SuperParserMill.parser();

    parser.parse_String("...");

    assertFalse(parser.hasErrors());
    assertEquals(1, ModifiedMCParserBase.customCalled);
  }
}
