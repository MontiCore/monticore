// (c) https://github.com/MontiCore/monticore

// (c) https://github.com/MontiCore/monticore

import de.se_rwth.commons.logging.Log;
import org.junit.Test;

public class GenerateAutomataParserTest {

  public void setup() {
    Log.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void test() {
    String[] args = {"src/test/resources/Automata.mc4", "target/gen"};
    GenerateAutomataParser.main(args);
  }
}
