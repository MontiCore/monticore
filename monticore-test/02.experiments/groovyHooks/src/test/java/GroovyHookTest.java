/* (c) https://github.com/MontiCore/monticore */

import automata.AutomataTool;
import automata._ast.ASTAutomaton;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Main class for the some Demonstration to Parse
 */
public class GroovyHookTest {

  private AutomataTool tool = new AutomataTool();

  /**
   * @throws IOException
   */
  @Test
  public void testGH1() throws IOException {
    ASTAutomaton ast = tool.parse("src/test/resources/example/PingPong.aut");
    assertNotNull(ast);
    assertEquals("PingPong", ast.getName());
    assertEquals(3, ast.countStates());
  }


}
