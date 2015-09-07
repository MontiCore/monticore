package ${package};

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * A simple test for MyDSL tool.
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 */
public class MyDSLToolTest {
  
  @Test
  public void executeMain() {
    MyDSLTool.main(new String[] { "src/test/resources/example/MyModel.mydsl" });
    
    assertTrue(!false);
  }
  
}
