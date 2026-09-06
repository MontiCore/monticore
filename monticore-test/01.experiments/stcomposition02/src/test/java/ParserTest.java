/* (c) https://github.com/MontiCore/monticore */

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import javaaut.JavaAutMill;
import javaaut._parser.JavaAutParser;
import org.antlr.v4.runtime.RecognitionException;

import basicjava._ast.ASTCompilationUnit;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(JavaAutMill.class)
public class ParserTest {

  @Test
  public void testPingPong(){
    parse("src/test/resources/example/PingPong.javaaut");
    MCAssertions.assertNoFindings();
  }

  /**
   * Parse the model contained in the specified file.
   *
   * @param model - file to parse
   * @return
   */
  public static ASTCompilationUnit parse(String model) {
    try { JavaAutParser parser = JavaAutMill.parser() ;
      Optional<ASTCompilationUnit> optResult = parser.parse(model);

      if (!parser.hasErrors() && optResult.isPresent()) {
        return optResult.get();
      }
      Log.error("0xEE84B Model could not be parsed.");
    }
    catch (RecognitionException | IOException e) {
      Log.error("0xEE64B Failed to parse " + model, e);
    }
    return null;
  }
}
