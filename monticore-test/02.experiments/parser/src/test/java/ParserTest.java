/* (c) https://github.com/MontiCore/monticore */

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import g1.G1Mill;
import g1._ast.ASTN;
import g1._parser.G1Parser;
import g3.G3Mill;
import g3._parser.G3Parser;
import g4.G4Mill;
import g4._parser.G4Parser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

public class ParserTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testG1ParserCanParseG4N() throws IOException {
    G4Mill.init();
    G1Parser parser = G1Mill.parser();
    Optional<ASTN> optionalN =  parser.parse_StringN("...N4T1");
    Assertions.assertTrue(optionalN.isPresent());
    Assertions.assertTrue(optionalN.get() instanceof g4._ast.ASTN);
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testG3ParserErrorWhenParsingG4N() throws IOException {
    G4Mill.init();
    G3Parser parser = G3Mill.parser();
    Optional<g3._ast.ASTN> optionalN = parser.parse_StringN("...N4T1");
    Assertions.assertFalse(optionalN.isPresent());
    Assertions.assertEquals(1, Log.getFindings().size());
    Assertions.assertTrue(Log.getFindings().get(0).getMsg().endsWith("Overridden production N is not reachable"));
  }

  @Test
  public void testG4ParserCanParseG4N() throws IOException {
    G4Parser parser = G4Mill.parser();
    Optional<g4._ast.ASTN> optionalN = parser.parse_StringN("...N4T1");
    Assertions.assertTrue(optionalN.isPresent());
    Assertions.assertTrue(optionalN.get() instanceof g4._ast.ASTN);
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}
