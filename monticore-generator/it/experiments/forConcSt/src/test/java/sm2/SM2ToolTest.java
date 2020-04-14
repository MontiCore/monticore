// (c) https://github.com/MontiCore/monticore

package sm2;

import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class SM2ToolTest {

  @Before
  public void setup() {
    Log.enableFailQuick(false);
  }

  @Test
  public void test() {
    String[] args = {"src/test/resources/example/PingPong.aut"};
    SM2Tool.main(args);
    assertTrue(Log.getFindings().isEmpty());
  }
}
