/* (c) https://github.com/MontiCore/monticore */

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import g.GMill;
import g._ast.ASTA;
import g._ast.ASTB;
import g._ast.ASTC;
import g._parser.GParser;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class GTest {
  
  @Before
  public void init() {
    LogStub.init();         // replace log by a sideffect free variant
    // LogStub.initPlusLog();  // for manual testing purpose only
    Log.enableFailQuick(false);
  }
  
  @Test
  public  void testG() throws IOException {
    GParser p = GMill.parser();
    Optional<ASTC> ast = p.parse_String("0, \"foo\"");
    assertTrue(ast.isPresent() && ast.get() instanceof ASTA);
    
    Optional<ASTC> ast2 = p.parse_String("\"foo\": 9");
    assertTrue(ast2.isPresent() && ast2.get() instanceof ASTB);
    
    
  }
  
}
