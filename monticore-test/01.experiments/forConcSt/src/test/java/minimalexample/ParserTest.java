package minimalexample;/* (c) https://github.com/MontiCore/monticore */

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import minimalexample._ast.ASTC;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParserTest {

  @Before
  public void setUp(){
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testCardinality() throws IOException {
    Optional<ASTC> ast = MinimalExampleMill.parser().parse_StringC("C foo 1");
    assertTrue(ast.isPresent());
    assertEquals(1, ast.get().getCARDINALITY());

    ast = MinimalExampleMill.parser().parse_StringC("C foo *");
    assertTrue(ast.isPresent());
    assertEquals(-1, ast.get().getCARDINALITY());
  }


}
