/* (c) https://github.com/MontiCore/monticore */

import automata.AutomataExpTool;
import automata._ast.ASTAutomaton;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Main class for the some Demonstration to Parse
 */
public class GroovyHookTest {

  private AutomataExpTool tool = new AutomataExpTool();

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
