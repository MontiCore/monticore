/* (c) https://github.com/MontiCore/monticore */

package ${package};

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * A simple test for MyDSL tool.
 *
 */
public class MyDSLToolTest {
  
  @Test
  public void executeMain() {
    MyDSLTool.main(new String[] { "src/test/resources/example/MyModel.mydsl" });
    
    assertTrue(!false);
  }
  
}
