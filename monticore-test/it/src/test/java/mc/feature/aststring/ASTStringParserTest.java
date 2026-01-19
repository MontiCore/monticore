/* (c) https://github.com/MontiCore/monticore */

package mc.feature.aststring;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.feature.aststring.aststring.AststringMill;
import mc.feature.aststring.aststring._ast.ASTTestSingleQuote;
import org.junit.jupiter.api.BeforeEach;

import mc.GeneratorIntegrationsTest;
import mc.feature.aststring.aststring._ast.ASTStart;
import mc.feature.aststring.aststring._parser.AststringParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
    assertTrue(opt.isPresent());
    ASTStart ast = opt.get();
    
    assertFalse(p.hasErrors());
    
    // Test parsing
    assertEquals("ah", ast.getAList().get(0));
    assertEquals("be", ast.getAList().get(1));
    assertEquals("ce", ast.getAList().get(2));
    assertEquals("oh", ast.getBList().get(0));
    assertEquals("pe", ast.getBList().get(1));
    assertEquals("qu", ast.getBList().get(2));
    assertEquals("x", ast.getCList().get(0));
    assertEquals("y", ast.getCList().get(1));
    assertEquals("z", ast.getCList().get(2));
    assertEquals("de", ast.getDList().get(0));
    assertEquals("eh", ast.getDList().get(1));
    
    // Test toString method
    assertEquals("ef", ast.getDList().get(2).toString());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSingleQuote() throws IOException {
    Optional<ASTTestSingleQuote> ast = AststringMill.parser().parse_StringTestSingleQuote("Alex's Parser probleme");
    assertTrue(ast.isPresent());
  }
  
}
