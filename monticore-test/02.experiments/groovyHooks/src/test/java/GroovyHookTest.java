/* (c) https://github.com/MontiCore/monticore */

import automata.AutomataTool;
import automata._ast.ASTAutomaton;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * Main class for the some Demonstration to Parse
 */
public class GroovyHookTest {

  private AutomataTool tool = new AutomataTool();
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  /**
   * @throws IOException
   */
  @Test
  public void testGH1() throws IOException {
    ASTAutomaton ast = tool.parse("src/test/resources/example/PingPong.aut");
    Assertions.assertNotNull(ast);
    Assertions.assertEquals("PingPong", ast.getName());
    Assertions.assertEquals(3, ast.countStates());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


}
