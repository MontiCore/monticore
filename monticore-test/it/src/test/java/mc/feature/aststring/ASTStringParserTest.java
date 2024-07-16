/* (c) https://github.com/MontiCore/monticore */

package mc.feature.aststring;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import mc.GeneratorIntegrationsTest;
import mc.feature.aststring.aststring._ast.ASTStart;
import mc.feature.aststring.aststring._parser.AststringParser;
import org.junit.jupiter.api.Test;

public class ASTStringParserTest extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testParser() throws IOException {
    
    StringReader s = new StringReader(
        "start ah be ce , oh pe qu , x.y.z , de eh ef");
    
    AststringParser p = new AststringParser();
    java.util.Optional<ASTStart> opt = p.parseStart(s);
    Assertions.assertTrue(opt.isPresent());
    ASTStart ast = opt.get();
    
    Assertions.assertEquals(false, p.hasErrors());
    
    // Test parsing
    Assertions.assertEquals("ah", ast.getAList().get(0));
    Assertions.assertEquals("be", ast.getAList().get(1));
    Assertions.assertEquals("ce", ast.getAList().get(2));
    Assertions.assertEquals("oh", ast.getBList().get(0));
    Assertions.assertEquals("pe", ast.getBList().get(1));
    Assertions.assertEquals("qu", ast.getBList().get(2));
    Assertions.assertEquals("x", ast.getCList().get(0));
    Assertions.assertEquals("y", ast.getCList().get(1));
    Assertions.assertEquals("z", ast.getCList().get(2));
    Assertions.assertEquals("de", ast.getDList().get(0));
    Assertions.assertEquals("eh", ast.getDList().get(1));
    
    // Test toString method
    Assertions.assertEquals("ef", ast.getDList().get(2).toString());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
}
