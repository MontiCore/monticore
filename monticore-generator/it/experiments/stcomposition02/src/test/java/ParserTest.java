/* (c) https://github.com/MontiCore/monticore */

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import javaaut._parser.JavaAutParser;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.BeforeClass;
import org.junit.Test;

import basicjava._ast.ASTCompilationUnit;
import java.io.IOException;
import java.util.Optional;

public class ParserTest {

  @BeforeClass
  public static void setUp(){
    LogStub.init();
  }

  @Test
  public void testPingPong(){
    parse("src/test/resources/example/PingPong.javaaut");
  }

  /**
   * Parse the model contained in the specified file.
   *
   * @param model - file to parse
   * @return
   */
  public static ASTCompilationUnit parse(String model) {
    try { JavaAutParser parser = new JavaAutParser() ;
      Optional<ASTCompilationUnit> optResult = parser.parse(model);

      if (!parser.hasErrors() && optResult.isPresent()) {
        return optResult.get();
      }
      Log.error("0xEE84B Model could not be parsed.");
    }
    catch (RecognitionException | IOException e) {
      Log.error("0xEE64B Failed to parse " + model, e);
    }
    System.exit(1);
    return null;
  }
}
