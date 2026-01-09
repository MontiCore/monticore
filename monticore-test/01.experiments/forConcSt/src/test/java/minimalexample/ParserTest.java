package minimalexample;/* (c) https://github.com/MontiCore/monticore */

import de.monticore.runtime.junit.TestWithMCLanguage;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import minimalexample._ast.ASTC;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(MinimalExampleMill.class)
public class ParserTest {

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
